package com.popcoadmin.content.repository;

import com.popcoadmin.content.entity.WatchProvider;
import com.popcoadmin.content.entity.key.ContentId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WatchProviderRepository extends JpaRepository<WatchProvider, Long> {
    void deleteByContent_Id(ContentId contentId);
}
