package com.daniinyan.votingmanager.service;

import com.daniinyan.votingmanager.domain.AgendaStatus;
import com.daniinyan.votingmanager.domain.Vote;
import com.daniinyan.votingmanager.domain.VoteResult;
import com.daniinyan.votingmanager.domain.VotingSession;
import com.daniinyan.votingmanager.exception.*;
import com.daniinyan.votingmanager.repository.VotingSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
public class VotingSessionService {

    private final VotingSessionRepository repository;
    private final AgendaService agendaService;

    @Autowired
    public VotingSessionService(VotingSessionRepository repository, AgendaService agendaService) {
        this.repository = repository;
        this.agendaService = agendaService;
    }

    public Mono<VotingSession> findByAgendaId(String agendaId) {
        return repository
                .findByAgendaId(agendaId)
                .switchIfEmpty(Mono.error(new IdNotFoundException()))
                .flatMap(this::update);
    }

    public Mono<VotingSession> save(VotingSession session) {
        if (session.getAgenda() == null || session.getAgenda().getId() == null) {
            throw new RequiredAgendaException();
        }

        String agendaId = session.getAgenda().getId();
        return agendaService.openAgenda(agendaId)
                .map(agenda -> {
                    session.setAgenda(agenda);
                    return session;
                })
                .map(this::setDefaultValues)
                .flatMap(repository::save);
    }

    public Mono<VotingSession> update(VotingSession session) {
        boolean isOpen = session.getAgenda().getStatus() != AgendaStatus.CLOSED;
        boolean isExpired = session.getEnd().isBefore(LocalDateTime.now());

        if (isOpen && isExpired) {
            String agendaId = session.getAgenda().getId();
            VoteResult result = calculateResult(session);
            return agendaService.close(agendaId, result)
                    .map(agenda -> {session.setAgenda(agenda); return session;});
        }

        return Mono.just(session);
    }

    public VotingSession setDefaultValues(VotingSession session) {
        if (session.getEnd() == null) {
            session.setEnd(LocalDateTime.now().plusMinutes(1L));
        }
        session.setStart(LocalDateTime.now());
        return session;
    }

    public Mono<VotingSession> addVoteToAgenda(String agendaId, Vote vote) {
        return repository.findByAgendaId(agendaId)
                .switchIfEmpty(Mono.error(new IdNotFoundException()))
                .map(session -> {
                    validateSessionToVote(session);
                    validateVote(vote, session);
                    session.addVote(vote);
                    return session;
                })
                .flatMap(repository::save);
    }

    public void validateSessionToVote(VotingSession session) {
        if (session.getAgenda().getStatus() != AgendaStatus.OPENED) {
            throw new AgendaException("Agenda isn't open.");
        }

        if (session.getEnd().isBefore(LocalDateTime.now())) {
            throw new AgendaException("Agenda is closed.");
        }
    }

    public void validateVote(Vote vote, VotingSession session) {
        if (vote.getValue() == null) {
            throw new InvalidVoteException("Must have a value.");
        }

        if (vote.getValue() != VoteResult.NO && vote.getValue() != VoteResult.YES) {
            throw new InvalidVoteException(
                    "Value must be YES or NO.");
        }

        if (session.getVotes().stream()
                .anyMatch(sessionVote ->
                        sessionVote.getAuthorId().equals(vote.getAuthorId()))) {
            throw new InvalidAuthorException("This author has already a vote on this session.");
        }
    }

    public VoteResult calculateResult(VotingSession session) {
        if (session.getVotes().isEmpty()) {
            return VoteResult.EMPTY;
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
