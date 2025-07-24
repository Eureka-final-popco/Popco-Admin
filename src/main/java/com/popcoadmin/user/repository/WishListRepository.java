package com.popcoadmin.user.repository;

import com.popcoadmin.user.entity.WishList;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WishListRepository extends CrudRepository<WishList, Long> {
}
