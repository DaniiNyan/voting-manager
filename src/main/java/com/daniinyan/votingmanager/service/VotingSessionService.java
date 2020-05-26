package com.daniinyan.votingmanager.service;

import com.daniinyan.votingmanager.domain.AgendaStatus;
import com.daniinyan.votingmanager.domain.Vote;
import com.daniinyan.votingmanager.domain.VoteValue;
import com.daniinyan.votingmanager.domain.VotingSession;
import com.daniinyan.votingmanager.exception.*;
import com.daniinyan.votingmanager.repository.VotingSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Arrays;

@Service
public class VotingSessionService {

    private VotingSessionRepository repository;

    @Autowired
    public VotingSessionService(VotingSessionRepository repository) {
        this.repository = repository;
    }

    public Flux<VotingSession> findAll() {
        return repository.findAll();
    }

    public Mono<VotingSession> save(VotingSession votingSession) {
        VotingSession validSession = validateSessionToOpen(votingSession);

        return findByAgendaId(validSession.getAgenda().getId())
                .switchIfEmpty(Mono.error(new IdNotFoundException()))
                .flatMap(session -> Mono.just(setDefaultValues(session)))
                .flatMap(session -> repository.save(session))
                .switchIfEmpty(Mono.error(new RuntimeException()));
    }

    public Mono<VotingSession> findByAgendaId(String agendaId) {
        return repository
                .findByAgendaId(agendaId)
                .switchIfEmpty(Mono.error(new IdNotFoundException()));
    }

    public Mono<VotingSession> addVoteToAgenda(String agendaId, Vote vote) {
        Mono<VotingSession> validSessionToVote = findByAgendaId(agendaId)
                .flatMap(session -> Mono.just(validateSessionToVote(session)));

        return validSessionToVote
                .flatMap(session -> {
                    Vote validVote = validateVote(vote, session);
                    session.addVote(validVote);
                    return repository.save(session);
                });
    }

    public VotingSession setDefaultValues(VotingSession session) {
        if (session.getEnd() == null) {
            session.setEnd(LocalDateTime.now().plusMinutes(1L));
        }
        session.setStart(LocalDateTime.now());
        session.getAgenda().setStatus(AgendaStatus.OPENED);

        return session;
    }

    public VotingSession validateSessionToOpen(VotingSession session) {
        if (session.getAgenda() == null) {
            throw new RequiredAgendaException();
        }

        if (session.getAgenda().getStatus() != AgendaStatus.NEW) {
            throw new OpenedSessionException();
        }
        return session;
    }

    public VotingSession validateSessionToVote(VotingSession session) {
        if (session.getAgenda().getStatus() != AgendaStatus.OPENED) {
            throw new AgendaException("Agenda isn't open.");
        }

        if (session.getEnd().isBefore(LocalDateTime.now())) {
            // closeSession(session);
            throw new AgendaException("Agenda is closed.");
        }

        return session;
    }

    public Vote validateVote(Vote vote, VotingSession session) {
        if (vote.getValue() == null) {
            throw new InvalidVoteException("Must have a value.");
        }

        if (vote.getValue() != VoteValue.NO && vote.getValue() != VoteValue.YES) {
            throw new InvalidVoteException(
                    "Must be an accepted value. Values: " + Arrays.toString(VoteValue.values()));
        }

        if (session.getVotes().stream()
                .anyMatch(sessionVote ->
                        sessionVote.getAuthorId().equals(vote.getAuthorId()))) {
            throw new InvalidAuthorException("This author has already a vote on this session.");
        }

        return vote;
    }

}
