package com.daniinyan.votingmanager.service;

import com.daniinyan.votingmanager.api.v1.controller.AgendaController;
import com.daniinyan.votingmanager.domain.Vote;
import com.daniinyan.votingmanager.domain.VotingSession;
import com.daniinyan.votingmanager.repository.VotingSessionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class VotingSessionService {

    private static final Logger logger = LoggerFactory.getLogger(VotingSessionService.class);
    private VotingSessionRepository repository;

    @Autowired
    public VotingSessionService(VotingSessionRepository repository) {
        this.repository = repository;
    }

    public Mono<VotingSession> addVoteToAgenda(String agendaId, Vote vote) {
        return repository.findByAgendaId(agendaId)
                .flatMap(session -> {
                    session.addVote(vote);
                    return repository.save(session);
                });
    }
}
