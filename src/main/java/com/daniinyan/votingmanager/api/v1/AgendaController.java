package com.daniinyan.votingmanager.api.v1;

import com.daniinyan.votingmanager.domain.Agenda;
import com.daniinyan.votingmanager.service.AgendaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1/agendas")
public class AgendaController {

    private AgendaService service;

    @Autowired
    public AgendaController(AgendaService service) {
        this.service = service;
    }


    @Operation(
            summary = "List all agendas",
            description = "Use to get all created agendas"
    )
    @GetMapping
    @ResponseBody
    public Flux<Agenda> findAll() {
        return service.findAll();
    }


    @Operation(
            summary = "Find by ID",
            description = "Use to find an agenda by ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "404", description = "Id not found"),
            }
    )
    @GetMapping("/{agendaId}")
    @ResponseBody
    public Mono<Agenda> findById(@PathVariable String agendaId) {
        return service.findById(agendaId);
    }

    @Operation(
            summary = "Create new agenda",
            description = "Use to create a new agenda",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Success"),
                    @ApiResponse(responseCode = "400", description = "Missing required fields"),
            }
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Agenda> save(@RequestBody Agenda agenda) {
        return service.save(agenda);
    }
}
