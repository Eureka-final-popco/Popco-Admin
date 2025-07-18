package com.popcoadmin.content.repository;

import com.popcoadmin.content.entity.ContentVideo;
import com.popcoadmin.content.entity.key.ContentId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContentVideoRepository extends JpaRepository<ContentVideo, String> {
    void deleteByContent_Id(ContentId contentId);
}
