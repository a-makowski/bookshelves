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

import com.makowski.bookshelves.dto.PasswordDto;
import com.makowski.bookshelves.dto.UserDto;
import com.makowski.bookshelves.entity.Rating;
import com.makowski.bookshelves.entity.Shelf;
import com.makowski.bookshelves.entity.User;
import com.makowski.bookshelves.exceptions.ErrorResponse;
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
@Tag(name = "User Controller", description = "User entity manager")
@RequestMapping("/user")
public class UserController {
       
    private UserService userService;

    @Operation(summary = "Create User", description = "Creates a user from the provided payload")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Successful creation of a new user", content = @Content(schema = @Schema(implementation = User.class))),
        @ApiResponse(responseCode = "400", description = "Invalid Request -  the request body requires an unique username, unique email adress, password and privacy status", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    }) 
    @PostMapping("/register")
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        return new ResponseEntity<>(userService.createUser(user), HttpStatus.CREATED);
    }

    @Operation(summary = "Get user by ID", description = "Returns a user based on an ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful retrieval of a user", content = @Content(schema = @Schema(implementation = User.class))),
        @ApiResponse(responseCode = "401", description = "JWT Token not valid", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "User doesn't exist in a database", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    }) 
    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        return new ResponseEntity<>(userService.getUser(id), HttpStatus.OK);
    }

    @Operation(summary = "Change password", description = "Changes password of a currently logged in user")    
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful password change"),
        @ApiResponse(responseCode = "400", description = "Invalid Request - New password and repeated password don't match", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "JWT Token not valid", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "403", description = "Current password does not match", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    }) 
    @PutMapping("/password")
    public ResponseEntity<HttpStatus> changePassword(@RequestBody PasswordDto password) {
        userService.changePassword(password);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "Delete user by ID", description = "Deletes a user based on an ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "User successfully deleted from a database"),
        @ApiResponse(responseCode = "401", description = "JWT Token not valid", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "403", description = "Access denied - an account can only be deleted by its owner", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "User doesn't exist in a database", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    }) 
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "Change privacy status", description = "Changes a privacy status of a currently logged in user to the opposite. A private user's library and ratings collection can only be viewed by its owner. As long as the account is marked as private, all ratings/reviews created by this user will be displayed without the username.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful privacy status change", content = @Content(schema = @Schema(implementation = User.class))),
        @ApiResponse(responseCode = "401", description = "JWT Token not valid", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    }) 
    @PutMapping("/privacy")
    public ResponseEntity<User> changePrivacyStatus() {
        return new ResponseEntity<>(userService.changePrivacyStatus(), HttpStatus.OK);
    }

    @Operation(summary = "Get library from a user", description = "Returns a collection of shelves based on their owner ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful retrieval of shelves", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Shelf.class)))),
        @ApiResponse(responseCode = "401", description = "JWT Token not valid", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "403", description = "This library is private, only its owner has access to it", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "User doesn't exist in a database", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    }) 
    @GetMapping("/{userId}/library")
    public ResponseEntity<List<Shelf>> getLibrary(@PathVariable Long userId) {
        return new ResponseEntity<>(userService.getUsersLibrary(userId), HttpStatus.OK);
    }

    @Operation(summary = "Get ratings from a user", description = "Returns a collection of all ratings made by a user, based on user's ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful retrieval of ratings", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Rating.class)))),
        @ApiResponse(responseCode = "401", description = "JWT Token not valid", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "403", description = "This account is private, only its owner can view ratings", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "User doesn't exist in a database or has not created any ratings", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    }) 
    @GetMapping("/{userId}/ratings")
    public ResponseEntity<List<Rating>> getRatings(@PathVariable Long userId) {
        return new ResponseEntity<>(userService.showUsersRatings(userId), HttpStatus.OK);
    }

    @Operation(summary = "Delete \"now reading\" status", description = "Changes a currently logged in user's \"now reading\" field to a null value")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Status successfully removed", content = @Content(schema = @Schema(implementation = User.class))),
        @ApiResponse(responseCode = "401", description = "JWT Token not valid", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    }) 
    @PutMapping
    public ResponseEntity<User> deleteNowReading() {
        return new ResponseEntity<>(userService.deleteNowReadingStatus(), HttpStatus.OK);
    }

    @Operation(summary = "Search by a phrase", description = "Returns a list of users whose username contains a given phrase. Character size is irrelevant")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful retrieval of a list of users", content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserDto.class)))),
        @ApiResponse(responseCode = "400", description = "Invalid Request - the search phrase must be specified in the request body", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "JWT Token not valid", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "There is no such user in the database", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    }) 
    @GetMapping("/search/{phrase}")
    public ResponseEntity<List<UserDto>> findUser(@PathVariable String phrase) {
        return new ResponseEntity<>(userService.findUser(phrase), HttpStatus.OK);
    }
}