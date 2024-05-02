package com.makowski.bookshelves.exceptions;

public class PasswordNotEqualsException extends RuntimeException {
    
    public PasswordNotEqualsException() {
        super("You provided 2 different passwords");
    }
}