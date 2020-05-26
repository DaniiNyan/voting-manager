package com.daniinyan.votingmanager.service;

import com.daniinyan.votingmanager.domain.Agenda;
import com.daniinyan.votingmanager.exception.IdNotFoundException;
import com.daniinyan.votingmanager.exception.RequiredNameException;
import com.daniinyan.votingmanager.repository.AgendaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class AgendaService {

    private AgendaRepository repository;

    @Autowired
    public AgendaService(AgendaRepository repository) {
        this.repository = repository;
    }

    public Flux<Agenda> findAll() {
        return repository.findAll();
    }

    public Mono<Agenda> findById(String agendaId) {
        return repository
                .findById(agendaId)
                .switchIfEmpty(Mono.error(new IdNotFoundException()));
    }

    public Mono<Agenda> save(Agenda agenda) {
        if (agenda.getName() == null) {
            throw new RequiredNameException();
        }

        return repository
                .save(agenda)
                .switchIfEmpty(Mono.error(new RuntimeException()));
    }
}
