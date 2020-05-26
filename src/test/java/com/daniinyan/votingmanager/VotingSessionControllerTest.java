package com.daniinyan.votingmanager;

import com.daniinyan.votingmanager.domain.Agenda;
import com.daniinyan.votingmanager.domain.AgendaStatus;
import com.daniinyan.votingmanager.domain.VoteValue;
import com.daniinyan.votingmanager.domain.VotingSession;
import com.daniinyan.votingmanager.repository.VotingSessionRepository;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;

import static org.mockito.BDDMockito.given;

@SpringBootTest
@AutoConfigureWebTestClient
public class VotingSessionControllerTest {

    @MockBean
    VotingSessionRepository sessionRepository;

    @Autowired
    WebTestClient client;

    @Test
    public void shouldCreateASession() {
        Agenda agenda = new Agenda("123", "TestAgenda", AgendaStatus.NEW, VoteValue.YES);
        VotingSession session = new VotingSession(agenda);

        given(sessionRepository.save(BDDMockito.any(VotingSession.class))).willReturn(Mono.just(session));
        given(sessionRepository.findByAgendaId("123")).willReturn(Mono.just(session));
        client.post()
                .uri("/session")
                .body(BodyInserters.fromValue(session))
                .exchange()
                .expectStatus().isCreated();
    }

    @Test
    public void shouldSetDefaultValuesOnCreate() {
        Agenda agenda = new Agenda("123", "TestAgenda", AgendaStatus.NEW, VoteValue.YES);
        VotingSession session = new VotingSession(agenda);

        given(sessionRepository.save(BDDMockito.any(VotingSession.class))).willReturn(Mono.just(session));
        given(sessionRepository.findByAgendaId("123")).willReturn(Mono.just(session));

        client.post()
                .uri("/session")
                .body(BodyInserters.fromValue(session))
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.agenda.status").isEqualTo("OPENED")
                .jsonPath("$.start").isNotEmpty()
                .jsonPath("$.end").isNotEmpty();
    }

    @Test
    public void shouldReturnBadRequestWhenSessionDoesNotHaveAgenda() {
        VotingSession session = new VotingSession();
        client.post()
                .uri("/session")
                .body(BodyInserters.fromValue(session))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    public void shouldReturnBadRequestWhenAgendaAlreadyHasASession() {
        Agenda agenda = new Agenda("123", "TestAgenda", AgendaStatus.OPENED, VoteValue.YES);
        VotingSession session = new VotingSession(agenda);

        given(sessionRepository.findByAgendaId("123")).willReturn(Mono.just(session));
        client.post()
                .uri("/session")
                .body(BodyInserters.fromValue(session))
                .exchange()
                .expectStatus().isBadRequest();
    }


}
