package com.popcoadmin.collection.repository;

import com.popcoadmin.collection.entity.CollectionContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CollectionContentRepository extends JpaRepository<CollectionContent, Long> {
}
