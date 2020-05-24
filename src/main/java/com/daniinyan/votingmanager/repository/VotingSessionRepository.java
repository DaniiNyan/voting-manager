package com.daniinyan.votingmanager.repository;

import com.daniinyan.votingmanager.domain.VotingSession;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface VotingSessionRepository extends ReactiveMongoRepository<VotingSession, String> {
    Mono<VotingSession> findByAgendaId(String agendaId);
}
