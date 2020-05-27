package com.daniinyan.votingmanager.service;

import com.daniinyan.votingmanager.domain.*;
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

        return repository.findByAgendaId(validSession.getAgenda().getId())
                .switchIfEmpty(Mono.error(new IdNotFoundException()))
                .flatMap(session -> Mono.just(setDefaultValues(session)))
                .flatMap(session -> repository.save(session))
                .switchIfEmpty(Mono.error(new RuntimeException()));
    }

    public Mono<VotingSession> findByAgendaId(String agendaId) {
        return repository
                .findByAgendaId(agendaId)
                .switchIfEmpty(Mono.error(new IdNotFoundException()))
                .flatMap(session -> {
                    if (session.getAgenda().getStatus() != AgendaStatus.CLOSED
                            && session.getEnd().isBefore(LocalDateTime.now())) {
                        return Mono.just(closeSession(session));
                    }
                    return Mono.just(session);
                });
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
        session.getAgenda().setResult(VoteResult.EMPTY);

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
            closeSession(session);
            throw new AgendaException("Agenda is closed.");
        }

        return session;
    }

    public Vote validateVote(Vote vote, VotingSession session) {
        if (vote.getValue() == null) {
            throw new InvalidVoteException("Must have a value.");
        }

        if (vote.getValue() != VoteResult.NO && vote.getValue() != VoteResult.YES) {
            throw new InvalidVoteException(
                    "Must be an accepted value. Values: " + Arrays.toString(VoteResult.values()));
        }

        if (session.getVotes().stream()
                .anyMatch(sessionVote ->
                        sessionVote.getAuthorId().equals(vote.getAuthorId()))) {
            throw new InvalidAuthorException("This author has already a vote on this session.");
        }

        return vote;
    }

    public VotingSession closeSession(VotingSession session) {
        session.getAgenda().setStatus(AgendaStatus.CLOSED);
        session.getAgenda().setResult(calculateResult(session));
        repository.save(session);
        return session;
    }

    public VoteResult calculateResult(VotingSession session) {
        if (session.getAgenda().getResult() == VoteResult.DRAW
                || session.getAgenda().getResult() == VoteResult.EMPTY) {
            return session.getAgenda().getResult();
        }

        long totalYes = session.getVotes().stream()
                .filter(vote -> vote.getValue() == VoteResult.YES)
                .count();

        long totalNo = session.getVotes().stream()
                .filter(vote -> vote.getValue() == VoteResult.NO)
                .count();

        if (totalYes == totalNo) {
            return VoteResult.DRAW;
        }

        return totalYes > totalNo ? VoteResult.YES : VoteResult.NO;

    }

}
