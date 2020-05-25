package com.daniinyan.votingmanager.exception;

public class RequiredNameException extends RuntimeException {
    public RequiredNameException() {
        super("Can't create agenda without name.");
    }
}
