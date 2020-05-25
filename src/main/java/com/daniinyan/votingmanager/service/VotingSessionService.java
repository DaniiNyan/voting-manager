package com.daniinyan.votingmanager.service;

import com.daniinyan.votingmanager.domain.SessionStatus;
import com.daniinyan.votingmanager.domain.Vote;
import com.daniinyan.votingmanager.domain.VotingSession;
import com.daniinyan.votingmanager.exception.IdNotFoundException;
import com.daniinyan.votingmanager.exception.RequiredAgendaException;
import com.daniinyan.votingmanager.repository.VotingSessionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
public class VotingSessionService {

    private VotingSessionRepository repository;

    @Autowired
    public VotingSessionService(VotingSessionRepository repository) {
        this.repository = repository;
    }

    public Flux<VotingSession> getAll() {
        return repository.findAll();
    }

    public Mono<VotingSession> open(VotingSession votingSession) {
        if (votingSession.getAgenda() == null) {
            throw new RequiredAgendaException();
        }

        return getByAgendaId(votingSession.getAgenda().getId())
                .flatMap(session -> repository.save(setDefaultValues(session)))
                .switchIfEmpty(Mono.error(new RuntimeException()));
    }

    public Mono<VotingSession> getByAgendaId(String agendaId) {
        return repository
                .findByAgendaId(agendaId)
                .switchIfEmpty(Mono.error(new IdNotFoundException()));
    }

    public Mono<VotingSession> addVoteToAgenda(String agendaId, Vote vote) {
        return getByAgendaId(agendaId)
                .flatMap(session -> {
                    session.addVote(vote);
                    return repository.save(session);
                });
    }

    private VotingSession setDefaultValues(VotingSession votingSession) {
        votingSession.setStart(LocalDateTime.now());
        votingSession.setStatus(SessionStatus.OPENED);
        if (votingSession.getEnd() == null) {
            votingSession.setEnd(LocalDateTime.now().plusMinutes(1L));
        }

        return votingSession;
    }


}
