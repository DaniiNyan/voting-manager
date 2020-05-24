package com.daniinyan.votingmanager.api.v1.controller;

import com.daniinyan.votingmanager.domain.Vote;
import com.daniinyan.votingmanager.domain.VotingSession;
import com.daniinyan.votingmanager.repository.VotingSessionRepository;
import com.daniinyan.votingmanager.service.VotingSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/session")
public class SessionController {

    private VotingSessionRepository repository;
    private VotingSessionService service;

    @Autowired
    public SessionController(VotingSessionRepository repository, VotingSessionService service) {
        this.repository = repository;
        this.service = service;
    }

    @GetMapping
    @ResponseBody
    // todo: show only opened sessions
    public Flux<VotingSession> getAllSessions() {
        return repository.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    // todo: validate fields and set default values
    public Mono<VotingSession> openSession(@RequestBody VotingSession votingSession) {
        return repository.save(votingSession);
    }

    @GetMapping("/{agendaId}")
    @ResponseBody
    public Mono<VotingSession> getSessionById(@PathVariable String agendaId) {
        return repository.findByAgendaId(agendaId);
    }

    @PatchMapping("/{agendaId}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<VotingSession> vote(@PathVariable String agendaId, @RequestBody Vote vote) {
        return service.addVoteToAgenda(agendaId, vote);
    }
}
