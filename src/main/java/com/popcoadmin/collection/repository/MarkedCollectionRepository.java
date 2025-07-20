package com.popcoadmin.collection.repository;

import com.popcoadmin.collection.entity.MarkedCollection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MarkedCollectionRepository extends JpaRepository<MarkedCollection, Long> {
}
