package com.makowski.bookshelves.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.makowski.bookshelves.dto.BookDto;
import com.makowski.bookshelves.entity.Book;
import com.makowski.bookshelves.entity.Rating;
import com.makowski.bookshelves.exceptions.EntityNotFoundException;
import com.makowski.bookshelves.exceptions.InvalidRequestException;
import com.makowski.bookshelves.repository.BookRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class BookService {

    private BookRepository bookRepository;

    public Book getBook(Long id) {
        return bookRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException(id, Book.class));
    }

    public Boolean existsById(Long id) {
        if (bookRepository.existsById(id)) return true;
            else return false;
    }

    public Book addBook(Book book) {          
        book.setRating(0);             
        book.setScoresNumber(0);
        book.setScoresSume(0);
        return saveBook(book); 
    } 

    public Book saveBook(Book book) {
        return bookRepository.save(book);
    }

    public void deleteBook(Long id) {           
        if (existsById(id)) bookRepository.deleteById(id);
            else throw new EntityNotFoundException(id, Book.class);
    }

    public Book updateBook(Long id, Book editedBook) {    
        Book book = getBook(id);
        book.setTitle(editedBook.getTitle());
        book.setAuthor(editedBook.getAuthor());
        book.setGenre(editedBook.getGenre());
        book.setPublisher(editedBook.getPublisher());
        book.setPages(editedBook.getPages());
        book.setYear(editedBook.getYear());
        return saveBook(book);                   
    }

    public BookDto getBookDto(Book book) {
        return new BookDto(book.getId(), book.getTitle(), book.getAuthor(), book.getYear(), book.getRating(), book.getScoresNumber());
    }

    public List<BookDto> findBooks(String phrase) {
        if (phrase.isBlank() || phrase.isEmpty()) throw new InvalidRequestException();
        List<BookDto> books = new ArrayList<>();
        for (Book book : bookRepository.findAll()) {
            String name = book.getTitle() + " " + book.getAuthor();
            if (name.toUpperCase().contains(phrase.toUpperCase())) {
                books.add(getBookDto(book));
            }
        }
        if (books.isEmpty()) throw new EntityNotFoundException();
            else return books;
    }    
    
    public List<BookDto> getAuthorsBooks(String author) {                      
        if (author.isBlank() || author.isEmpty()) throw new InvalidRequestException();
        List<BookDto> books = new ArrayList<>();
        for (Book book : bookRepository.findByAuthorOrderByYearDesc(author)) 
            books.add(getBookDto(book));
        if (books.isEmpty()) throw new EntityNotFoundException();     
            else return books;
    }

    public List<Rating> getBooksRatings(Long id) {
        List<Rating> ratings = getBook(id).getRatings();
        if (ratings.isEmpty()) throw new EntityNotFoundException();
        for (Rating rating : ratings) {
            if (rating.getUser().getPrivateProfile()) rating.setUser(null);
        }
        return ratings;        
    }

    public List<BookDto> top10FromGenre(String genre) {      
        if (genre.isBlank() || genre.isEmpty()) throw new InvalidRequestException();
        List<BookDto> books = new ArrayList<>();
        for (Book book : bookRepository.findTop10ByGenreOrderByRatingDesc(genre)) {
            books.add(getBookDto(book));
        }
        if (books.isEmpty()) throw new EntityNotFoundException();     
            else return books;
    }
}