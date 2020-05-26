package com.daniinyan.votingmanager.api.v1.controller;

import com.daniinyan.votingmanager.domain.Vote;
import com.daniinyan.votingmanager.domain.VotingSession;
import com.daniinyan.votingmanager.service.VotingSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/session")
public class VotingSessionController {

    private VotingSessionService service;

    @Autowired
    public VotingSessionController(VotingSessionService service) {
        this.service = service;
    }

    @GetMapping
    @ResponseBody
    // todo: show only opened sessions
    public Flux<VotingSession> getAll() {
        return service.getAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<VotingSession> openSession(@RequestBody VotingSession votingSession) {
        return service.open(votingSession);
    }

    @GetMapping("/{agendaId}")
    @ResponseBody
    public Mono<VotingSession> getSessionByAgendaId(@PathVariable String agendaId) {
        return service.getByAgendaId(agendaId);
    }

    @PatchMapping("/{agendaId}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<VotingSession> vote(@PathVariable String agendaId, @RequestBody Vote vote) {
        return service.addVoteToAgenda(agendaId, vote);
    }
}
