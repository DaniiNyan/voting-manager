package com.daniinyan.votingmanager.exception;

public class InvalidAuthorException extends RuntimeException {
    public InvalidAuthorException(String message) {
        super("Invalid author: " + message);
    }
}
