package com.makowski.bookshelves.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.makowski.bookshelves.dto.BookDto;
import com.makowski.bookshelves.entity.Book;
import com.makowski.bookshelves.entity.Rating;
import com.makowski.bookshelves.entity.Shelf;
import com.makowski.bookshelves.entity.User;
import com.makowski.bookshelves.exceptions.ErrorResponse;
import com.makowski.bookshelves.service.BookService;
import com.makowski.bookshelves.service.ShelfService;
import com.makowski.bookshelves.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@Tag(name = "Book Controller", description = "Book entity manager")
@RequestMapping("/book")
public class BookController {
    
    private BookService bookService;
    private ShelfService shelfService;
    private UserService userService;

    @Operation(summary = "Get book by ID", description = "Returns a book based on an ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful retrieval of a book", content = @Content(schema = @Schema(implementation = Book.class))),
        @ApiResponse(responseCode = "401", description = "JWT Token not valid", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Book doesn't exist in a database", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    }) 
    @GetMapping("/{id}")
    public ResponseEntity<Book> getBook(@PathVariable Long id) {
        return new ResponseEntity<>(bookService.getBook(id), HttpStatus.OK);
    }

    @Operation(summary = "Add book", description = "Adds a new book to the database")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Successful creation of book", content = @Content(schema = @Schema(implementation = Book.class))),
        @ApiResponse(responseCode = "400", description = "Invalid Request - request body have to contain valid data, such as a title, an author, a publisher, a genre, pages number and year of publication", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "JWT Token not valid", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    }) 
    @PostMapping
    public ResponseEntity<Book> addBook(@Valid @RequestBody Book book) {
        return new ResponseEntity<>(bookService.addBook(book), HttpStatus.CREATED);
    }

    @Operation(summary = "Delete book by ID", description = "Deletes a book based on an ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Book successfully deleted from a database"),
        @ApiResponse(responseCode = "401", description = "JWT Token not valid", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Book doesn't exist in a database", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    }) 
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "Edit book", description = "Allows to change some book data, such as title, author, publisher, genre, pages number and year of publication, based on an ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Book has been successfully edited", content = @Content(schema = @Schema(implementation = Book.class))),
        @ApiResponse(responseCode = "400", description = "Invalid Request - request body have to contain valid data, such as a title, an author, a publisher, a genre, pages number and year of publication", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "JWT Token not valid", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Book doesn't exist in a database", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    }) 
    @PutMapping("/{id}")
    public ResponseEntity<Book> updateBook(@Valid @RequestBody Book book, @PathVariable Long id) {
        return new ResponseEntity<>(bookService.updateBook(id, book), HttpStatus.OK);
    }

    @Operation(summary = "Add book to a shelf", description = "Adds a book with a selected ID to a shelf with a selected ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful addition of a book to the bookshelf", content = @Content(schema = @Schema(implementation = Shelf.class))),
        @ApiResponse(responseCode = "400", description = "Invalid Request - the selected book is already on the selected shelf", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "JWT Token not valid", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "403", description = "Only an owner of a shelf is allowed to add books to it", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Book or shelf doesn't exist in a database", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    }) 
    @PutMapping("/{shelfId}/{bookId}")
    public ResponseEntity<Shelf> addBookToShelf(@PathVariable Long shelfId, @PathVariable Long bookId) {
        return new ResponseEntity<>(shelfService.addToShelf(bookId, shelfId), HttpStatus.OK);
    }

    @Operation(summary = "Set as \"Now Reading\"", description = "Sets an ID of a book in the \"now reading\" field from a currently logged in user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Book ID successfully added to “now reading” section", content = @Content(schema = @Schema(implementation = User.class))),
        @ApiResponse(responseCode = "401", description = "JWT Token not valid", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Book doesn't exist in a database", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    }) 
    @PutMapping("/reading/{id}")
    public ResponseEntity<User> setAsNowReading(@PathVariable Long id) {
        return new ResponseEntity<>(userService.setAsNowReading(id), HttpStatus.OK);
    }

    @Operation(summary = "Search by phrase", description = "Returns a list of books whose title or author's name contains a given phrase. Character size is irrelevant")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful retrieval of list of books", content = @Content(array = @ArraySchema(schema = @Schema(implementation = BookDto.class)))),
        @ApiResponse(responseCode = "400", description = "Invalid Request - the search phrase must be specified in the request body", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "JWT Token not valid", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "There's no such book in a database", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    }) 
    @GetMapping("/search/{phrase}")
    public ResponseEntity<List<BookDto>> findBooks(@PathVariable String phrase) {
        return new ResponseEntity<>(bookService.findBooks(phrase), HttpStatus.OK);
    }

    @Operation(summary = "Get author's books", description = "Returns a list of all books written by the selected author, sorted by a year of publication")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful retrieval of a list of books", content = @Content(array = @ArraySchema(schema = @Schema(implementation = BookDto.class)))),
        @ApiResponse(responseCode = "400", description = "Invalid Request - an author's name must be specified in the request body", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "JWT Token not valid", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "There's no books written by selected author in a database", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    }) 
    @GetMapping("/author/{author}")
    public ResponseEntity<List<BookDto>> getAuthorsBooks(@PathVariable String author) {
        return new ResponseEntity<>(bookService.getAuthorsBooks(author), HttpStatus.OK);
    }
    
    @Operation(summary = "Get book's ratings", description = "Returns a list of all scores and reviews of a selected book, based on ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful retrieval of a list of ratings", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Rating.class)))),
        @ApiResponse(responseCode = "401", description = "JWT Token not valid", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Book doesn't exist in a database or has no ratings", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    }) 
    @GetMapping("/{id}/ratings")
    public ResponseEntity<List<Rating>> getBooksRatings(@PathVariable Long id) {
        return new ResponseEntity<>(bookService.getBooksRatings(id), HttpStatus.OK);
    }

    @Operation(summary = "Get top10 from genre", description = "Returns a list of 10 best rated books in a selected genre")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful retrieval of a list of books", content = @Content(array = @ArraySchema(schema = @Schema(implementation = BookDto.class)))),
        @ApiResponse(responseCode = "400", description = "Invalid Request - the genre must be specified in the request body ", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "JWT Token not valid", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "There's no books of selected genre in a database", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    }) 
    @GetMapping("/top/{genre}")
    public ResponseEntity<List<BookDto>> getTop10FromGenre(@PathVariable String genre) {
        return new ResponseEntity<>(bookService.top10FromGenre(genre), HttpStatus.OK);
    } 
}
