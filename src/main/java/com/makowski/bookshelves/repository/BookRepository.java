package com.makowski.bookshelves.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.makowski.bookshelves.entity.Book;

public interface BookRepository extends CrudRepository<Book, Long> {

    List<Book> findByAuthorOrderByYearDesc(String author);

    List<Book> findTop10ByGenreOrderByRatingDesc(String genre);

}