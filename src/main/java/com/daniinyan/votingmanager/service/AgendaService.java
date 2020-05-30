package com.daniinyan.votingmanager.service;

import com.daniinyan.votingmanager.domain.Agenda;
import com.daniinyan.votingmanager.domain.AgendaStatus;
import com.daniinyan.votingmanager.domain.VoteResult;
import com.daniinyan.votingmanager.exception.IdNotFoundException;
import com.daniinyan.votingmanager.exception.RequiredNameException;
import com.daniinyan.votingmanager.exception.VotingSessionException;
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

        agenda.setStatus(AgendaStatus.NEW);
        agenda.setResult(VoteResult.EMPTY);

        return repository
                .save(agenda)
                .switchIfEmpty(Mono.error(new RuntimeException()));
    }

    public Mono<Agenda> openAgenda(String agendaId) {
        return repository.findById(agendaId)
                .switchIfEmpty(Mono.error(new IdNotFoundException()))
                .map(agenda -> {
                    if (agenda.getStatus() == AgendaStatus.OPENED) {
                        throw new VotingSessionException("There're already an opened session with this agenda.");
                    }
                    if (agenda.getStatus() == AgendaStatus.CLOSED) {
                        throw new VotingSessionException("This agenda was already closed.");
                    }
                    agenda.setId(agendaId);
                    agenda.setStatus(AgendaStatus.OPENED);
                    return agenda;
                })
                .flatMap(updatedAgenda -> repository.save(updatedAgenda));
    }
}
