package com.daniinyan.votingmanager.domain;

public class Vote {
    private VoteResult value;
    private String cpf;

    public Vote() {
    }

    public Vote(VoteResult value, String cpf) {
        this.value = value;
        this.cpf = cpf;
    }

    public VoteResult getValue() {
        return value;
    }

    public void setValue(VoteResult value) {
        this.value = value;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }
}
