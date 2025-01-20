package com.makowski.bookshelves.repository;

import org.springframework.data.repository.CrudRepository;

import com.makowski.bookshelves.entity.Shelf;

public interface ShelfRepository extends CrudRepository<Shelf, Long> {

}