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
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
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
public class DailyPopularContentJobConfig {

    public static final String JOB_NAME = "popularContentJob";

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final EntityManagerFactory entityManagerFactory;
    private final ContentReactionRepository contentReactionRepository;

    @Bean
    public Job popularContentJob() {
        return new JobBuilder(JOB_NAME, jobRepository)
                .start(clearPreviousDataStep()) // 기존 데이터 제거
                .next(buildPopularContentStep(BatchContentType.ALL))
                .next(buildPopularContentStep(BatchContentType.MOVIE))
                .next(buildPopularContentStep(BatchContentType.TV))
                .build();
    }

    // 이전 데이터 삭제 스텝
    @Bean
    public Step clearPreviousDataStep() {
        return new StepBuilder("clearPreviousDataStep", jobRepository)
                .tasklet(clearPreviousDataTasklet(), platformTransactionManager)
                .build();
    }

    public Step buildPopularContentStep(BatchContentType type) {
        return new StepBuilder("popularContentStep_" + type.name(), jobRepository)
                .<PopularContentStats, DailyPopularContent>chunk(100, platformTransactionManager)
                .reader(createReader(type.getValue()))
                .processor(popularContentProcessor(type))
                .writer(popularContentWriter())
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
