package com.makowski.bookshelves.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.makowski.bookshelves.entity.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    Optional<User> findByUsername(String username);

    boolean existsByUsernameIgnoreCase(String username);

    boolean existsByEmailIgnoreCase(String email);

}