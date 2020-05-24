package com.daniinyan.votingmanager.exception;

public class IdNotFoundException extends RuntimeException {
    public IdNotFoundException() {
        super("Id not found.");
    }
}
