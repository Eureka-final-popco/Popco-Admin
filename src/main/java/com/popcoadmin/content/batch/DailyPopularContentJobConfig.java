package com.popcoadmin.content.batch;

import com.popcoadmin.content.dto.response.content.PopularContentStats;
import com.popcoadmin.content.entity.DailyPopularContent;
import com.popcoadmin.content.entity.enums.BatchContentType;
import com.popcoadmin.content.repository.ContentReactionRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EnableBatchProcessing
@Configuration
@RequiredArgsConstructor
@Slf4j
public class DailyPopularContentJobConfig {

    public static final String JOB_NAME = "popularContentJob";

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final EntityManagerFactory entityManagerFactory;
    private final ContentReactionRepository contentReactionRepository;
    private final BatchFailureLoggingListener batchFailureLoggingListener;

    @Bean
    public Job popularContentJob() {
        Step stepAll = buildPopularContentStep(BatchContentType.ALL);
        Step stepMovie = buildPopularContentStep(BatchContentType.MOVIE);
        Step stepTv = buildPopularContentStep(BatchContentType.TV);

        return new JobBuilder(JOB_NAME, jobRepository)
                .start(clearPreviousDataStep())
                .next(stepAll)
                .on("*").to(stepMovie)
                .from(stepMovie)
                .on("*").to(stepTv)
                .from(stepTv)
                .on("*").end()
                .build().build();
    }

    // 이전 데이터 삭제 스텝
    @Bean
    public Step clearPreviousDataStep() {
        return new StepBuilder("clearPreviousDataStep", jobRepository)
                .tasklet(clearPreviousDataTasklet(), platformTransactionManager)
                .listener(batchFailureLoggingListener)
                .build();
    }

    private JobExecutionDecider deciderStep() {
        return (jobExecution, stepExecution) -> {
            String type = jobExecution.getJobParameters().getString("type");
            if (type == null) {
                return new FlowExecutionStatus("FAILED");
            }
            if (!(type.equalsIgnoreCase("ALL") || type.equalsIgnoreCase("MOVIE") || type.equalsIgnoreCase("TV"))) {
                return new FlowExecutionStatus("FAILED");
            }
            return new FlowExecutionStatus(type.toUpperCase());
        };
    }

    @Bean
    public Job retrySingleStepJob() {
        JobExecutionDecider decider = deciderStep(); // 직접 생성

        return new JobBuilder("retrySingleStepJob", jobRepository)
                .start(decider)
                .from(decider).on("ALL").to(buildPopularContentStep(BatchContentType.ALL))
                .from(decider).on("MOVIE").to(buildPopularContentStep(BatchContentType.MOVIE))
                .from(decider).on("TV").to(buildPopularContentStep(BatchContentType.TV))
                .from(decider).on("FAILED").fail()
                .end()
                .build();
    }

    public Step buildPopularContentStep(BatchContentType type) {
        return new StepBuilder("popularContentStep_" + type.name(), jobRepository)
                .<PopularContentStats, DailyPopularContent>chunk(100, platformTransactionManager)
                .reader(createReader(type.getValue()))
                .processor(popularContentProcessor(type))
                .writer(popularContentWriter())
                .listener(batchFailureLoggingListener)
                .build();
    }

    @Bean
    public Tasklet clearPreviousDataTasklet() {
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                EntityManager em = entityManagerFactory.createEntityManager();
                EntityTransaction tx = em.getTransaction();

                try {
                    tx.begin();
                    LocalDate yesterday = LocalDate.now().minusDays(2);

                    Query query = em.createQuery(
                            "DELETE FROM DailyPopularContent p WHERE p.rankedDate = :targetDate"
                    );
                    query.setParameter("targetDate", yesterday);
                    int deletedCount = query.executeUpdate();

                    tx.commit();
                    System.out.println("이전 인기 콘텐츠 데이터 " + deletedCount + "건 삭제 완료");

                } catch (Exception e) {
                    if (tx.isActive()) {
                        tx.rollback();
                    }
                    throw e;
                } finally {
                    em.close();
                }

                return RepeatStatus.FINISHED;
            }
        };
    }

    public RepositoryItemReader<PopularContentStats> createReader(String type) {
        RepositoryItemReader<PopularContentStats> reader = new RepositoryItemReader<>();
        reader.setRepository(contentReactionRepository);
        reader.setMethodName("findPopularContentStatsByType");

        LocalDate settingDay = LocalDate.now().minusDays(7);
        LocalDateTime startOfDay = settingDay.atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(7);

        List<Object> arguments = Arrays.asList(
                startOfDay,
                endOfDay,
                type
        );
        reader.setArguments(arguments);

        // Pageable에서 정렬 처리
        Map<String, Sort.Direction> sorts = new HashMap<>();
        sorts.put("likeCount", Sort.Direction.DESC);
        reader.setSort(sorts);

        reader.setPageSize(100);
        reader.setSaveState(false);

        return reader;
    }

    public ItemProcessor<PopularContentStats, DailyPopularContent> popularContentProcessor(BatchContentType type) {
        return new ItemProcessor<>() {
            private int rank = 1;
            private final LocalDate rankedDate = LocalDate.now().minusDays(1);

            @Override
            public DailyPopularContent process(PopularContentStats stats) {
                if (rank > 5) {
                    return null;
                }
                return DailyPopularContent.builder()
                        .content(stats.getContent())
                        .likeCount(stats.getLikeCount())
                        .rankedDate(rankedDate)
                        .ranking(rank++)
                        .batchContentType(type.name())
                        .build();
            }
        };
    }

    @Bean
    public JpaItemWriter<DailyPopularContent> popularContentWriter() {
        JpaItemWriter<DailyPopularContent> writer = new JpaItemWriter<>();
        writer.setEntityManagerFactory(entityManagerFactory);
        return writer;
    }
}
