package com.daniinyan.votingmanager.exception;

public class RequiredAgendaException extends RuntimeException {
    public RequiredAgendaException() {
        super("Can't open session without an agenda.");
    }
}
