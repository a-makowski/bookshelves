package com.makowski.bookshelves.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.makowski.bookshelves.entity.Book;
import com.makowski.bookshelves.entity.Rating;
import com.makowski.bookshelves.entity.User;

public interface RatingRepository extends CrudRepository<Rating, Long> {
    
    List<Rating> findByBook(Book book);
    List<User> findByUser(User user);
}
