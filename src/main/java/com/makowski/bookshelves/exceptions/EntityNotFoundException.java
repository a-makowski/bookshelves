package com.makowski.bookshelves.exceptions;

public class EntityNotFoundException extends RuntimeException {
    
    public EntityNotFoundException(Long id, Class<?> entity) {
        super("The " + entity.getSimpleName().toLowerCase() + " with id '" + id + "' does not exist.");
    }

    public EntityNotFoundException(String username) {
        super("User \"" + username + "\" does not exist.");
    }

    public EntityNotFoundException(Long bookId, Long shelfId) {
        super("There's no book with ID " + bookId + " on the shelf with ID " + shelfId + ".");
    }

    public EntityNotFoundException() {
        super("No results");
    }
}
