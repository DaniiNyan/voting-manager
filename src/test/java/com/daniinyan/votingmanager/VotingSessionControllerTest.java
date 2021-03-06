package com.daniinyan.votingmanager;

import com.daniinyan.votingmanager.domain.*;
import com.daniinyan.votingmanager.repository.AgendaRepository;
import com.daniinyan.votingmanager.repository.VotingSessionRepository;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.BDDMockito.given;

@SpringBootTest(properties = {"springdoc.api-docs.enabled=false"})
@AutoConfigureWebTestClient
public class VotingSessionControllerTest {

    @MockBean
    VotingSessionRepository sessionRepository;

    @MockBean
    AgendaRepository agendaRepository;

    @Autowired
    WebTestClient client;

    @Test
    public void shouldCreateSession() {
        Agenda agenda = new Agenda("TestAgenda");
        agenda.setId("123");
        agenda.setStatus(AgendaStatus.NEW);
        VotingSession session = new VotingSession(agenda);

        given(agendaRepository.findById("123")).willReturn(Mono.just(agenda));
        given(agendaRepository.save(BDDMockito.any(Agenda.class))).willReturn(Mono.just(agenda));
        given(sessionRepository.save(BDDMockito.any(VotingSession.class))).willReturn(Mono.just(session));
        client.post()
                .uri("/v1/sessions")
                .body(BodyInserters.fromValue(session))
                .exchange()
                .expectStatus().isCreated();
    }

    @Test
    public void shouldReturnBadRequestWhenSessionDoesNotHaveAgenda() {
        VotingSession session = new VotingSession();
        client.post()
                .uri("/v1/sessions")
                .body(BodyInserters.fromValue(session))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    public void shouldReturnMethodNotAllowedWhenAgendaAlreadyHasSession() {
        Agenda agenda = new Agenda("123", "TestAgenda", AgendaStatus.OPENED, VoteResult.YES);
        VotingSession session = new VotingSession(agenda);

        given(agendaRepository.findById("123")).willReturn(Mono.just(agenda));
        client.post()
                .uri("/v1/sessions")
                .body(BodyInserters.fromValue(session))
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
    }

    @Test
    public void shouldReturnNotFoundWhenSearchingByNonexistentAgenda() {
        given(sessionRepository.findByAgendaId("456")).willReturn(Mono.empty());
        client.get()
                .uri("/v1/sessions/456")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    public void shouldReturnCorrectSessionWhenSearchingByAgendaId() {
        Agenda agenda = new Agenda("123", "TestAgenda", AgendaStatus.NEW, null);
        VotingSession session = new VotingSession("345",
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(1),
                agenda,
                null);
        given(sessionRepository.findByAgendaId("123")).willReturn(Mono.just(session));

        client.get()
                .uri("/v1/sessions/123")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.agenda.id").isEqualTo("123")
                .jsonPath("$.agenda.name").isEqualTo("TestAgenda")
                .jsonPath("$.agenda.status").isEqualTo("NEW")
                .jsonPath("$.id").isEqualTo("345")
                .jsonPath("$.votes").isEmpty();
    }

    @Test
    public void shouldAddVote() {
        Agenda agenda = new Agenda("123", "TestAgenda", AgendaStatus.OPENED, null);
        VotingSession session = new VotingSession(agenda);
        session.setEnd(LocalDateTime.now().plusHours(1));
        Vote vote = new Vote(VoteResult.YES, "321");

        VotingSession sessionWithVote = new VotingSession(agenda);
        sessionWithVote.addVote(vote);

        given(sessionRepository.findByAgendaId("123")).willReturn(Mono.just(session));
        given(sessionRepository.save(BDDMockito.any(VotingSession.class))).willReturn(Mono.just(sessionWithVote));
        client.patch()
                .uri("/v1/sessions/123")
                .body(BodyInserters.fromValue(vote))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.votes").isNotEmpty();
    }

    @Test
    public void shouldReturnBadRequestWhenVoteIsNull() {
        Agenda agenda = new Agenda("123", "TestAgenda", AgendaStatus.OPENED, null);
        VotingSession session = new VotingSession(agenda);
        session.setEnd(LocalDateTime.now().plusHours(1));
        Vote vote = new Vote(null, "321");

        given(sessionRepository.findByAgendaId("123")).willReturn(Mono.just(session));
        client.patch()
                .uri("/v1/sessions/123")
                .body(BodyInserters.fromValue(vote))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    public void shouldReturnBadRequestWhenVoteHasInvalidValue() {
        Agenda agenda = new Agenda("123", "TestAgenda", AgendaStatus.OPENED, null);
        VotingSession session = new VotingSession(agenda);
        session.setEnd(LocalDateTime.now().plusHours(1));
        Vote vote = new Vote(VoteResult.DRAW, "321");

        given(sessionRepository.findByAgendaId("123")).willReturn(Mono.just(session));
        client.patch()
                .uri("/v1/sessions/123")
                .body(BodyInserters.fromValue(vote))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    public void shouldReturnBadRequestWhenSessionAlreadyHasVoteFromAuthor() {
        Agenda agenda = new Agenda("123", "TestAgenda", AgendaStatus.OPENED, null);
        VotingSession session = new VotingSession(agenda);
        Vote vote = new Vote(VoteResult.YES, "321");
        session.addVote(vote);
        session.setEnd(LocalDateTime.now().plusHours(1));

        given(sessionRepository.findByAgendaId("123")).willReturn(Mono.just(session));
        client.patch()
                .uri("/v1/sessions/123")
                .body(BodyInserters.fromValue(vote))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    public void shouldReturnMethodNotAllowedWhenTryingToVoteOnNotOpenedSession() {
        Agenda agenda = new Agenda("123", "TestAgenda", AgendaStatus.NEW, null);
        VotingSession session = new VotingSession(agenda);
        session.setEnd(LocalDateTime.now().plusHours(1));
        Vote vote = new Vote(VoteResult.YES, "321");

        given(sessionRepository.findByAgendaId("123")).willReturn(Mono.just(session));
        client.patch()
                .uri("/v1/sessions/123")
                .body(BodyInserters.fromValue(vote))
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
    }

    @Test
    public void shouldReturnMethodNotAllowedWhenTryingToVoteOnSessionAfterEnd() {
        Agenda agenda = new Agenda("123", "TestAgenda", AgendaStatus.NEW, null);
        VotingSession session = new VotingSession(agenda);
        Vote vote = new Vote(VoteResult.YES, "321");
        session.setEnd(LocalDateTime.now().minusHours(1));

        given(sessionRepository.findByAgendaId("123")).willReturn(Mono.just(session));
        client.patch()
                .uri("/v1/sessions/123")
                .body(BodyInserters.fromValue(vote))
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
    }

    @Test
    public void shouldSetAgendaAsClosedWhenTryingToGetAfterEnd() {
        Agenda agenda = new Agenda("123", "TestAgenda", AgendaStatus.OPENED, null);
        VotingSession session = new VotingSession(agenda);
        session.setEnd(LocalDateTime.now().minusHours(1));

        List<Vote> votes = Arrays.asList(new Vote(VoteResult.YES, "321"),
                new Vote(VoteResult.YES, "654"));
        Agenda savedAgenda = new Agenda("123", "TestAgenda", AgendaStatus.CLOSED, VoteResult.YES);
        VotingSession savedSession = new VotingSession("123", LocalDateTime.now().minusHours(1), LocalDateTime.now().minusMinutes(20), savedAgenda, votes);

        given(sessionRepository.findByAgendaId("123")).willReturn(Mono.just(session));
        given(agendaRepository.findById("123")).willReturn(Mono.just(agenda));
        given(agendaRepository.save(BDDMockito.any(Agenda.class))).willReturn(Mono.just(savedAgenda));
        given(sessionRepository.save(BDDMockito.any(VotingSession.class))).willReturn(Mono.just(savedSession));
        client.get()
                .uri("/v1/sessions/123")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.agenda.id").isEqualTo("123")
                .jsonPath("$.agenda.name").isEqualTo("TestAgenda")
                .jsonPath("$.agenda.status").isEqualTo("CLOSED");
    }

    @Test
    public void shouldCalculateAgendaResultWhenSessionIsClosed() {
        Agenda agenda = new Agenda("123", "TestAgenda", AgendaStatus.OPENED, null);
        VotingSession session = new VotingSession(agenda);
        List<Vote> votes = Arrays.asList(new Vote(VoteResult.YES, "321"),
                new Vote(VoteResult.YES, "654"),
                new Vote(VoteResult.YES, "987"),
                new Vote(VoteResult.NO, "123"),
                new Vote(VoteResult.NO, "456"));
        session.setVotes(votes);
        session.setEnd(LocalDateTime.now().minusHours(1));
        Agenda savedAgenda = new Agenda("123", "TestAgenda", AgendaStatus.CLOSED, VoteResult.YES);
        VotingSession savedSession = new VotingSession("123", LocalDateTime.now().minusHours(1), LocalDateTime.now().minusMinutes(20), savedAgenda, votes);

        given(sessionRepository.findByAgendaId("123")).willReturn(Mono.just(session));
        given(agendaRepository.findById("123")).willReturn(Mono.just(agenda));
        given(agendaRepository.save(BDDMockito.any(Agenda.class))).willReturn(Mono.just(savedAgenda));
        given(sessionRepository.save(BDDMockito.any(VotingSession.class))).willReturn(Mono.just(savedSession));
        client.get()
                .uri("/v1/sessions/123")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.agenda.id").isEqualTo("123")
                .jsonPath("$.agenda.name").isEqualTo("TestAgenda")
                .jsonPath("$.agenda.status").isEqualTo("CLOSED")
                .jsonPath("$.votes").isNotEmpty()
                .jsonPath("$.agenda.result").isEqualTo("YES");
    }
}
