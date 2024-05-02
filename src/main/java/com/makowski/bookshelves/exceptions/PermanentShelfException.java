package com.makowski.bookshelves.exceptions;

public class PermanentShelfException extends RuntimeException {

    public PermanentShelfException(String activity) {
        super("This shelf is permanent, you can't " + activity + " it.");
    }
}
