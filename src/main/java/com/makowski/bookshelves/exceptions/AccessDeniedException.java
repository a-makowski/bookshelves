package com.makowski.bookshelves.exceptions;

public class AccessDeniedException extends RuntimeException {
    public AccessDeniedException() {
        super("You have no access to this data");
    }    
}
