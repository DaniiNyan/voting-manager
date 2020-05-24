package com.daniinyan.votingmanager.repository;

import com.daniinyan.votingmanager.domain.Agenda;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AgendaRepository extends ReactiveMongoRepository<Agenda, String> {
}
