package com.daniinyan.votingmanager.api.v1;

import com.daniinyan.votingmanager.domain.Vote;
import com.daniinyan.votingmanager.domain.VotingSession;
import com.daniinyan.votingmanager.service.VotingSessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1/sessions")
public class VotingSessionController {

    private final VotingSessionService service;

    @Autowired
    public VotingSessionController(VotingSessionService service) {
        this.service = service;
    }

    @Operation(
            summary = "Create new session",
            description = "Use to create a new session",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Success"),
                    @ApiResponse(responseCode = "400", description = "Missing required fields"),
                    @ApiResponse(responseCode = "404", description = "Agenda ID not found"),
                    @ApiResponse(responseCode = "405", description = "Can't open session with this agenda")
            }
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<VotingSession> save(@RequestBody VotingSession votingSession) {
        return service.save(votingSession);
    }

    @Operation(
            summary = "Find by agenda ID",
            description = "Use to find a created session by its agenda ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "404", description = "Agenda ID not found")
            }
    )
    @GetMapping("/{agendaId}")
    @ResponseBody
    public Mono<VotingSession> findByAgendaId(@PathVariable String agendaId) {
        return service.findByAgendaId(agendaId);
    }

    @Operation(
            summary = "Add a vote to session",
            description = "Use to add a vote to an open session",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "400", description = "Invalid vote"),
                    @ApiResponse(responseCode = "404", description = "Agenda ID not found"),
                    @ApiResponse(responseCode = "405", description = "Can't vote in this agenda")
            }
    )
    @PatchMapping("/{agendaId}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<VotingSession> addVoteToAgenda(@PathVariable String agendaId, @RequestBody Vote vote) {
        return service.addVoteToAgenda(agendaId, vote);
    }
}
