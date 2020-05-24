package com.daniinyan.votingmanager.exception;

public class RequiredNameException extends RuntimeException {
    public RequiredNameException() {
        super("Name must have a value.");
    }
}
