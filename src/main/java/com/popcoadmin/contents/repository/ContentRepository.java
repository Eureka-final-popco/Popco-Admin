package com.popcoadmin.contents.repository;

import com.popcoadmin.contents.entity.Content;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContentRepository extends JpaRepository <Content, Integer> {
}
