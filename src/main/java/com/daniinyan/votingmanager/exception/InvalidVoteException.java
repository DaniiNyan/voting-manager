package com.daniinyan.votingmanager.exception;

public class InvalidVoteException extends RuntimeException {
    public InvalidVoteException(String message) {
        super("Invalid vote: " + message);
    }
}
