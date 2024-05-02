package com.makowski.bookshelves.controller;

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

import com.makowski.bookshelves.entity.Shelf;
import com.makowski.bookshelves.exceptions.ErrorResponse;
import com.makowski.bookshelves.service.ShelfService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@Tag(name = "Shelf Controller", description = "Shelf entity manager")
@RequestMapping("/shelf")
public class ShelfController {

    private ShelfService shelfService;

    @Operation(summary = "Get shelf by ID", description = "Returns a shelf based on an ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful retrieval of a shelf", content = @Content(schema = @Schema(implementation = Shelf.class))),
        @ApiResponse(responseCode = "401", description = "JWT Token not valid", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "403", description = "This shelf is private, only its owner has access to it", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Shelf doesn't exist in a database", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    }) 
    @GetMapping("/{id}")
    public ResponseEntity<Shelf> getShelf(@PathVariable Long id) {
        return new ResponseEntity<>(shelfService.showShelf(id), HttpStatus.OK);
    }

    @Operation(summary = "Create own shelf", description = "Creates a new shelf with a selected name for a currently logged in user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Successful creation of a shelf", content = @Content(schema = @Schema(implementation = Shelf.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request - the request body requires a new shelf name that does not exist in the currently logged in user's library", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "JWT Token not valid", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    }) 
    @PostMapping
    public ResponseEntity<Shelf> createOwnShelf(@RequestBody String name) {
        return new ResponseEntity<>(shelfService.createOwnShelf(name), HttpStatus.CREATED);
    }

    @Operation(summary = "Delete shelf by ID", description = "Deletes a shelf based on an ID. Some shelves are automatically created with a new account, they cannot be deleted.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Shelf successfully deleted from a database"),
        @ApiResponse(responseCode = "400", description = "Invalid request - permanent shelves cannot be deleted", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "JWT Token not valid", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "403", description = "Access denied - Shelf can only be deleted by its owner", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Shelf doesn't exist in a database", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    }) 
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteShelf(@PathVariable Long id) {
        shelfService.deleteShelf(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "Rename shelf", description = "Changes name of a shelf based on an ID. Some shelves are automatically created with a new account, they cannot be renamed.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Shelf has been successfully renamed", content = @Content(schema = @Schema(implementation = Shelf.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request - the request body requires a new shelf name that does not exist in its owner's library. Only non-permanent shelves can be renamed.", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "JWT Token not valid", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "403", description = "Shelves can only be renamed by their owner", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Shelf doesn't exist in a database", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    }) 
    @PutMapping("/{id}")
    public ResponseEntity<Shelf> renameShelf(@PathVariable Long id, @RequestBody String newName) {
        return new ResponseEntity<>(shelfService.renameShelf(id, newName), HttpStatus.OK);
    }

    @Operation(summary = "Delete book from shelf", description = "Removes a book with a selected ID from a shelf with a selected ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Book successfully deleted from the shelf", content = @Content(schema = @Schema(implementation = Shelf.class))),
        @ApiResponse(responseCode = "401", description = "JWT Token not valid", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "403", description = "Only the owner of a shelf can delete books from it.", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "A book or/and a shelf doesn't exist in a database or a selected book is not on a selected shelf", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{shelfId}/{bookId}")            
    public ResponseEntity<Shelf> deleteBookFromShelf(@PathVariable Long shelfId, @PathVariable Long bookId) {
        return new ResponseEntity<>(shelfService.deleteBookFromShelf(bookId, shelfId), HttpStatus.OK);
    }
}
