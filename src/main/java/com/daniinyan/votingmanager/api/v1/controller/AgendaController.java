package com.daniinyan.votingmanager.api.v1.controller;

import com.daniinyan.votingmanager.domain.Agenda;
import com.daniinyan.votingmanager.repository.AgendaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/agenda")
public class AgendaController {

    private static final Logger logger = LoggerFactory.getLogger(AgendaController.class);
    private AgendaRepository repository;

    @Autowired
    public AgendaController(AgendaRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    @ResponseBody
    public Flux<Agenda> getAllAgendas() {
        return repository.findAll();
    }

    @GetMapping("/{agendaId}")
    @ResponseBody
    public Mono<Agenda> getAgendaById(@PathVariable String agendaId) {
        return repository.findById(agendaId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Agenda> createAgenda(@RequestBody Agenda agenda) {
        return repository.save(agenda);
    }
}
