package com.popcoadmin.contents.repository;

import com.popcoadmin.contents.entity.OTT;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OTTRepository extends JpaRepository<OTT, Long> {
}
