package com.makowski.bookshelves.exceptions;

public class InvalidRequestException extends RuntimeException {
    
    public InvalidRequestException() {
        super("Invalid request.");
    }
}
