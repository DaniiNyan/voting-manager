package com.daniinyan.votingmanager.domain;

import org.springframework.data.annotation.Id;

import java.util.List;

public class Agenda {

    @Id
    private String id;
    private String name;
    private VoteValue result;

    public Agenda() {}

    public Agenda(String id, String name, VoteValue result) {
        this.id = id;
        this.name = name;
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
