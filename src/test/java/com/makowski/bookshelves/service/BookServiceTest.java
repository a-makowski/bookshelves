package com.makowski.bookshelves.service;

import com.makowski.bookshelves.dto.BookDto;
import com.makowski.bookshelves.entity.Book;
import com.makowski.bookshelves.entity.Rating;
import com.makowski.bookshelves.entity.User;
import com.makowski.bookshelves.exceptions.EntityNotFoundException;
import com.makowski.bookshelves.exceptions.InvalidRequestException;
import com.makowski.bookshelves.repository.BookRepository;
import com.makowski.bookshelves.testutils.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @InjectMocks
    BookService bookService;
    @Mock
    BookRepository bookRepository;

    @Test
    void getBook_ReturnsBook_WhenBookExists () {
        Book book = TestDataFactory.createTestBook();

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        Book result = bookService.getBook(1L);

        assertEquals(book.getTitle(), result.getTitle());
    }

    @Test
    void getBook_ThrowsException_WhenBookDoesNotExist() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> bookService.getBook(1L));
    }

    @Test
    void addBook_AddsBookWithDefaultRatingValues_WhenBookIsProvided() {
        Book book = TestDataFactory.createTestBook();

        when(bookRepository.save(book)).thenReturn(book);

        Book result = bookService.addBook(book);

        assertEquals(book.getTitle(), result.getTitle());
        assertEquals(0, result.getRating());
        assertEquals(0, result.getScoresNumber());
        assertEquals(0, result.getScoresSum());
        verify(bookRepository).save(book);
    }

    @Test
    void deleteBook_DeletesBook_WhenBookExists() {
        when(bookRepository.existsById(1L)).thenReturn(true);

        bookService.deleteBook(1L);

        verify(bookRepository).deleteById(1L);
    }

    @Test
    void deleteBook_ThrowsException_WhenBookDoesNotExist() {
        when(bookRepository.existsById(1L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> bookService.deleteBook(1L));
    }

    @Test
    void updateBook_UpdatesBook_WhenBookExists() {
        Book book = TestDataFactory.createTestBook();
        Book editedBook = TestDataFactory.createTestBook();
        editedBook.setId(2L);
        editedBook.setTitle("edited title");
        editedBook.setAuthor("edited author");
        editedBook.setGenre("edited genre");
        editedBook.setPublisher("edited publisher");
        editedBook.setPages(100);
        editedBook.setYear(2000);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookRepository.save(book)).thenReturn(book);

        Book result = bookService.updateBook(1L, editedBook);

        assertEquals(book.getId(), result.getId());
        assertEquals(book.getRating(), result.getRating());

        assertEquals(editedBook.getTitle(), result.getTitle());
        assertEquals(editedBook.getAuthor(), result.getAuthor());
        assertEquals(editedBook.getGenre(), result.getGenre());
        assertEquals(editedBook.getPublisher(), result.getPublisher());
        assertEquals(editedBook.getPages(), result.getPages());
        assertEquals(editedBook.getYear(), result.getYear());

        verify(bookRepository).save(book);
    }

    @Test
    void updateBook_ThrowsException_WhenBookDoesNotExist() {
        Book book = TestDataFactory.createTestBook();

        when(bookRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> bookService.updateBook(2L, book));
    }

    @Test
    void getBookDto_ReturnsBookDto_WhenBookProvided() {
        Book book = TestDataFactory.createTestBook();

        BookDto result = bookService.getBookDto(book);

        assertEquals(book.getId(), result.getId());
        assertEquals(book.getTitle(), result.getTitle());
        assertEquals(book.getAuthor(), result.getAuthor());
        assertEquals(book.getYear(), result.getYear());
        assertEquals(book.getRating(), result.getRating());
        assertEquals(book.getScoresNumber(), result.getScoresNumber());
    }

    @Test
    void findBooks_ReturnsListOfBooksDto_WhenBooksAreFound() {
        List<Book> books = TestDataFactory.createMoreTestBooks();

        when(bookRepository.findAll()).thenReturn(books);

        List<BookDto> result = bookService.findBooks("title1");

        assertEquals(3, result.size());
    }

    @Test
    void findBooks_ThrowsException_WhenSearchPhraseIsBlank() {
        assertThrows(InvalidRequestException.class, () -> bookService.findBooks("     "));
    }

    @Test
    void findBooks_ThrowsException_WhenBooksNotFound() {
        List<Book> books = TestDataFactory.createMoreTestBooks();

        when(bookRepository.findAll()).thenReturn(books);

        assertThrows(EntityNotFoundException.class, () -> bookService.findBooks("Star Wars"));
    }

    @Test
    void getAuthorsBooks_ReturnsListOfAuthorsBooksDto_WhenBooksAreFound() {
        List<Book> allBooks = TestDataFactory.createMoreTestBooks();
        List<Book> authorsBooks = new ArrayList<>();
        authorsBooks.add(allBooks.get(1));
        authorsBooks.add(allBooks.get(3));
        authorsBooks.add(allBooks.get(0));
        authorsBooks.add(allBooks.get(2));

        when(bookRepository.findByAuthorOrderByYearDesc("author2")).thenReturn(authorsBooks);

        List<BookDto> result = bookService.getAuthorsBooks("author2");

        assertEquals(4, result.size());
        assertEquals(2015, result.get(0).getYear());
    }

    @Test
    void getAuthorsBooks_ThrowsException_WhenSearchPhraseIsBlank() {
        assertThrows(InvalidRequestException.class, () -> bookService.getAuthorsBooks(" "));
    }

    @Test
    void getAuthorsBooks_ThrowsException_WhenBooksNotFound() {
        when(bookRepository.findByAuthorOrderByYearDesc("author2")).thenReturn(Collections.emptyList());

        assertThrows(EntityNotFoundException.class, () -> bookService.getAuthorsBooks("author2"));
    }

    @Test
    void getBookRatings_ReturnsRatings_WhenRatingsExist() {
        User user1 = TestDataFactory.createTestUser();
        User user2 = TestDataFactory.createAnotherTestUser();
        user2.setPrivateProfile(true);

        Rating rating1 = TestDataFactory.createTestRating();
        rating1.setUser(user1);
        Rating rating2 = TestDataFactory.createTestRating();
        rating2.setId(2L);
        rating2.setUser(user2);

        List<Rating> ratings = new ArrayList<>();
        ratings.add(rating1);
        ratings.add(rating2);

        Book book = TestDataFactory.createTestBook();
        book.setRatings(ratings);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        List<Rating> result = bookService.getBookRatings(1L);

        assertEquals(2, result.size());
        assertEquals(user1, result.get(0).getUser());
        assertNull(result.get(1).getUser());
    }

    @Test
    void getBookRatings_ThrowsException_WhenRatingsDoNotExist() {
        Book book = TestDataFactory.createTestBook();

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        assertThrows(EntityNotFoundException.class, () -> bookService.getBookRatings(1L));
    }

    @Test
    void top10FromGenre_ReturnsListOfBooksDto_WhenBooksAreFound() {
        List<Book> books = TestDataFactory.createMoreTestBooks();
        books.remove(10);

        when(bookRepository.findTop10ByGenreOrderByRatingDesc("genre2")).thenReturn(books);

        List<BookDto> result = bookService.top10FromGenre("genre2");

        assertEquals(10, result.size());
        assertEquals("title2", result.get(0).getTitle());
    }

    @Test
    void top10FromGenre_ThrowsException_WhenSearchPhraseIsBlank() {
        assertThrows(InvalidRequestException.class, () -> bookService.top10FromGenre(""));
    }

    @Test
    void top10FromGenre_ThrowsException_WhenBooksNotFound() {
        when(bookRepository.findTop10ByGenreOrderByRatingDesc("genre2")).thenReturn(Collections.emptyList());

        assertThrows(EntityNotFoundException.class, () -> bookService.top10FromGenre("genre2"));
    }
}