package com.makowski.bookshelves;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.makowski.bookshelves.repository.BookRepository;

import lombok.AllArgsConstructor;

@SpringBootApplication
@AllArgsConstructor							
public class BookshelvesApplication implements CommandLineRunner {

	BookRepository bookRepository;

	public static void main(String[] args) {
		SpringApplication.run(BookshelvesApplication.class, args);
	}

	@Override			
	public void run(String... args) throws Exception {
	}
}