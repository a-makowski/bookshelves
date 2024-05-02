package com.makowski.bookshelves.exceptions;

public class ForbiddenNameException extends RuntimeException {

    public ForbiddenNameException() {
        super("This name is already taken, please try another one.");
    }

    public ForbiddenNameException(String email) {
        super("There is already an account for " + email);
    }
}
