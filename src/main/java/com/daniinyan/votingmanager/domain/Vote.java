package com.daniinyan.votingmanager.domain;

public class Vote {
    private VoteValue value;
    private String authorId;

    public Vote() {
    }

    public Vote(VoteValue value, String authorId) {
        this.value = value;
        this.authorId = authorId;
    }

    public VoteValue getValue() {
        return value;
    }

    public void setValue(VoteValue value) {
        this.value = value;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }
}
