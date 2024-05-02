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

import com.makowski.bookshelves.entity.Rating;
import com.makowski.bookshelves.exceptions.ErrorResponse;
import com.makowski.bookshelves.service.RatingService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@Tag(name = "Rating Controller", description = "Rating entity manager")
@RequestMapping("/rating")
public class RatingController {
 
    private RatingService ratingService;
    
    @Operation(summary = "Get rating by ID", description = "Returns a rating based on an ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful retrieval of a rating", content = @Content(schema = @Schema(implementation = Rating.class))),
        @ApiResponse(responseCode = "401", description = "JWT Token not valid", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Rating doesn't exist in a database", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    }) 
    @GetMapping("/{id}")
    public ResponseEntity<Rating> getRating(@PathVariable Long id) {
        return new ResponseEntity<>(ratingService.showRating(id), HttpStatus.OK);
    }

    @Operation(summary = "Create rating", description = "Creates a new rating, which include a review or a score from 1 to 10, or both. Score = 0 is a blank score and doesn't count toward the book's rating.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Successful creation of a rating", content = @Content(schema = @Schema(implementation = Rating.class))),
        @ApiResponse(responseCode = "400", description = "Invalid Request - rating must include a review or a score from 1 to 10. Each user can only add one rating to each book.", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "JWT Token not valid", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Book doesn't exist in a database", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    }) 
    @PostMapping("/{bookId}")
    public ResponseEntity<Rating> createRating(@RequestBody Rating rating, @PathVariable Long bookId) {
        return new ResponseEntity<>(ratingService.createRating(rating, bookId), HttpStatus.CREATED);
    }

    @Operation(summary = "Edit rating", description = "Allows to edit score or/and review based on an ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Rating has been successfully edited", content = @Content(schema = @Schema(implementation = Rating.class))),
        @ApiResponse(responseCode = "400", description = "Invalid Request - rating must include a review or a score from 1 to 10", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "JWT Token not valid", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "403", description = "Access denied - rating can only be edited by its creator", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Rating doesn't exist in a database", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    }) 
    @PutMapping("/{ratingId}")
    public ResponseEntity<Rating> updateRating(@PathVariable Long ratingId, @RequestBody Rating newRating) {
        return new ResponseEntity<>(ratingService.updateRating(ratingId, newRating), HttpStatus.OK);
    }
    
    @Operation(summary = "Delete rating by ID", description = "Deletes a rating based on an ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Rating successfully deleted from a database"),
        @ApiResponse(responseCode = "401", description = "JWT Token not valid", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "403", description = "Access denied - rating can only be delete by its creator", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Rating doesn't exist in a database", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    }) 
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteRating(@PathVariable Long id) {
        ratingService.deleteRating(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}