package com.makowski.bookshelves.repository;

import com.makowski.bookshelves.entity.Book;
import com.makowski.bookshelves.entity.User;
import org.springframework.data.repository.CrudRepository;

import com.makowski.bookshelves.entity.Rating;

public interface RatingRepository extends CrudRepository<Rating, Long> {

    boolean existsByUserAndBook(User user, Book book);

}