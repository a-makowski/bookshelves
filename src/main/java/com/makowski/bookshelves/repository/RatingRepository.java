package com.makowski.bookshelves.repository;

import org.springframework.data.repository.CrudRepository;

import com.makowski.bookshelves.entity.Rating;

public interface RatingRepository extends CrudRepository<Rating, Long> {

    boolean existByUserIdAndBookId(Long userId, Long bookId);

}