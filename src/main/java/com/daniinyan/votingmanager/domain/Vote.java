package com.daniinyan.votingmanager.domain;

public class Vote {
    private VoteResult value;
    private String authorId;

    public Vote() {
    }

    public Vote(VoteResult value, String authorId) {
        this.value = value;
        this.authorId = authorId;
    }

    public VoteResult getValue() {
        return value;
    }

    public void setValue(VoteResult value) {
        this.value = value;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }
}
