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
    public Flux<Agenda> findAll() {
        return service.findAll();
    }

    @GetMapping("/{agendaId}")
    @ResponseBody
    public Mono<Agenda> findById(@PathVariable String agendaId) {
        return service.findById(agendaId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Agenda> save(@RequestBody Agenda agenda) {
        return service.save(agenda);
    }
}
