package com.daniinyan.votingmanager.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Document
public class VotingSession {

    @Id
    private String id;
    private LocalDateTime start;
    private LocalDateTime end;
    private LocalTime durationTime;
    private SessionStatus status;
    private Agenda agenda;
    private List<Vote> votes = new ArrayList<>();

    public VotingSession() {
    }

    public VotingSession(String id, LocalDateTime start, LocalDateTime end, LocalTime durationTime, SessionStatus status, Agenda agenda, List<Vote> votes) {
        this.id = id;
        this.start = start;
        this.end = end;
        this.durationTime = durationTime;
        this.status = status;
        this.agenda = agenda;
        this.votes = votes;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }

    public LocalTime getDurationTime() {
        return durationTime;
    }

    public void setDurationTime(LocalTime durationTime) {
        this.durationTime = durationTime;
    }

    public SessionStatus getStatus() {
        return status;
    }

    public void setStatus(SessionStatus status) {
        this.status = status;
    }

    public Agenda getAgenda() {
        return agenda;
    }

    public void setAgenda(Agenda agenda) {
        this.agenda = agenda;
    }

    public List<Vote> getVotes() {
        return votes;
    }

    public void setVotes(List<Vote> votes) {
        this.votes = votes;
    }

    public void addVote(Vote vote) {
        this.votes.add(vote);
    };
}
