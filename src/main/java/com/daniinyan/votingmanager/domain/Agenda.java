package com.daniinyan.votingmanager.domain;

import org.springframework.data.annotation.Id;

public class Agenda {

    @Id
    private String id;
    private String name;
    private AgendaStatus status;
    private VoteResult result;

    public Agenda() {}

    public Agenda(String id, String name, AgendaStatus status, VoteResult result) {
        this.id = id;
        this.name = name;
        this.status = status;
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

    public VoteResult getResult() {
        return result;
    }

    public void setResult(VoteResult result) {
        this.result = result;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public AgendaStatus getStatus() {
        return status;
    }

    public void setStatus(AgendaStatus status) {
        this.status = status;
    }
}
