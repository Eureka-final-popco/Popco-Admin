package com.popcoadmin.contents.repository;

import com.popcoadmin.contents.entity.ContentDirector;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContentDirectorRepository extends JpaRepository<ContentDirector, Long> {
}
