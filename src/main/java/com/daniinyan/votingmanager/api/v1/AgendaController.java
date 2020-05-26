package com.daniinyan.votingmanager.api.v1;

import com.daniinyan.votingmanager.domain.Agenda;
import com.daniinyan.votingmanager.service.AgendaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/agenda")
public class AgendaController {

    private AgendaService service;

    @Autowired
    public AgendaController(AgendaService service) {
        this.service = service;
    }

    @GetMapping
    @ResponseBody
    public Flux<Agenda> getAll() {
        return service.getAll();
    }

    @GetMapping("/{agendaId}")
    @ResponseBody
    public Mono<Agenda> getAgendaById(@PathVariable String agendaId) {
        return service.getById(agendaId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Agenda> createAgenda(@RequestBody Agenda agenda) {
        return service.create(agenda);
    }
}
