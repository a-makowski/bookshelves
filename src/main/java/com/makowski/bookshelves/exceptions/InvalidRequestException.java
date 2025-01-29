package com.makowski.bookshelves.exceptions;

public class InvalidRequestException extends RuntimeException {

    public InvalidRequestException(String message) {
        super("Invalid request: " + message);
    }
}