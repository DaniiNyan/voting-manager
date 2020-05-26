package com.daniinyan.votingmanager.service;

import com.daniinyan.votingmanager.domain.AgendaStatus;
import com.daniinyan.votingmanager.domain.Vote;
import com.daniinyan.votingmanager.domain.VotingSession;
import com.daniinyan.votingmanager.exception.IdNotFoundException;
import com.daniinyan.votingmanager.exception.OpenedSessionException;
import com.daniinyan.votingmanager.exception.RequiredAgendaException;
import com.daniinyan.votingmanager.repository.VotingSessionRepository;
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

    public Flux<VotingSession> findAll() {
        return repository.findAll();
    }

    public Mono<VotingSession> save(VotingSession votingSession) {
        VotingSession validSession = validateSession(votingSession);

        return findByAgendaId(validSession.getAgenda().getId())
                .switchIfEmpty(Mono.error(new RequiredAgendaException()))
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
        return findByAgendaId(agendaId)
                .flatMap(session -> {
                    session.addVote(vote);
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

    public VotingSession validateSession(VotingSession session) {
        if (session.getAgenda() == null) {
            throw new RequiredAgendaException();
        }

        if (session.getAgenda().getStatus() != AgendaStatus.NEW) {
            throw new OpenedSessionException();
        }
        return session;
    }


}
