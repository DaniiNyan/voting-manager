package com.daniinyan.votingmanager.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document
public class Agenda {

    @Id
    private String id;
    private String name;
    private List<Vote> votes;
    private VoteValue result;

    public Agenda() {}

    public Agenda(String id, String name, List<Vote> votes, VoteValue result) {
        this.id = id;
        this.name = name;
        this.votes = votes;
        this.result = result;
    }

    public Agenda(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Vote> getVotes() {
        return votes;
    }

    public void setVotes(List<Vote> votes) {
        this.votes = votes;
    }

    public VoteValue getResult() {
        return result;
    }

    public void setResult(VoteValue result) {
        this.result = result;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
