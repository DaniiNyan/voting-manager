package com.daniinyan.votingmanager.exception;

public class OpenedSessionException extends RuntimeException {
    public OpenedSessionException() {
        super("There're already an opened session with this agenda.");
    }
}
