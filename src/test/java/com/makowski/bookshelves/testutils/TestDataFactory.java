package com.makowski.bookshelves.testutils;

import com.makowski.bookshelves.entity.Book;
import com.makowski.bookshelves.entity.Rating;
import com.makowski.bookshelves.entity.Shelf;
import com.makowski.bookshelves.entity.User;

import java.util.ArrayList;
import java.util.List;

public class TestDataFactory {

    public static Book createTestBook() {
        return createNewBook(1L, "title1", "author1", "genre1", 2025, 10.0F, 1);
    }

    public static User createTestUser() {
        return createNewUser(1L, "username1");
    }

    public static User createAnotherTestUser() {
        return createNewUser(2L, "username2");
    }

    public static Shelf createTestShelf() {
        return createNewShelf(3L, "Test Shelf", false);
    }

    public static List<Shelf> createTestDefaultLibrary() {
        List<Shelf> library = new ArrayList<>();
        library.add(createNewShelf(1L, "Want read", true));
        library.add(createNewShelf(2L, "Have read", true));
        return library;
    }

    public static Rating createTestRating() {
        Rating rating = new Rating();
        rating.setId(1L);
        rating.setScore(0);
        rating.setReview("");
        return rating;
    }

    public static List<Book> createMoreTestBooks() {
        List<Book> books = new ArrayList<>();
        books.add(createNewBook(2L, "title2", "author2", "genre2", 1990, 9.3F, 4));
        books.add(createNewBook(3L, "title3", "author2", "genre2", 2015, 8.6F, 15));
        books.add(createNewBook(4L, "title4", "author2", "genre2", 1989, 8.3F, 28));
        books.add(createNewBook(5L, "title5", "author2", "genre2", 2001, 7.4F, 3));
        books.add(createNewBook(6L, "title6", "author3", "genre2", 2024, 7.1F, 67));
        books.add(createNewBook(7L, "title7", "author4", "genre2", 2024, 7.0F, 17));
        books.add(createNewBook(8L, "title8", "author5", "genre2", 2024, 6.9F, 4));
        books.add(createNewBook(9L, "title9", "author6", "genre2", 2024, 6.7F, 45));
        books.add(createNewBook(10L, "title10", "author7", "genre2", 2024, 6.6F, 24));
        books.add(createNewBook(11L, "title11", "author8", "genre2", 2024, 6.2F, 5));
        books.add(createNewBook(12L, "title12", "author9", "genre2", 2024, 5.3F, 70));
        return books;
    }

    private static Book createNewBook(Long id, String title, String author, String genre, int year, float rating, int scoresNumber) {
        Book book = new Book();
        book.setId(id);
        book.setTitle(title);
        book.setAuthor(author);
        book.setGenre(genre);
        book.setYear(year);
        book.setRating(rating);
        book.setScoresNumber(scoresNumber);
        book.setRatings(new ArrayList<>());
        return book;
    }

    private static User createNewUser(Long id, String username) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setPrivateProfile(false);
        user.setRatings(new ArrayList<>());
        user.setShelves(new ArrayList<>());
        return user;
    }

    private static Shelf createNewShelf(Long id, String name, boolean permanent) {
        Shelf shelf = new Shelf();
        shelf.setId(id);
        shelf.setName(name);
        shelf.setPermanent(permanent);
        shelf.setBooks(new ArrayList<>());
        return shelf;
    }
}