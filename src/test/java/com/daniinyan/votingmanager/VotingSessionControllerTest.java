package com.daniinyan.votingmanager;

import com.daniinyan.votingmanager.domain.*;
import com.daniinyan.votingmanager.repository.VotingSessionRepository;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

import static org.mockito.BDDMockito.given;

@SpringBootTest
@AutoConfigureWebTestClient
public class VotingSessionControllerTest {

    @MockBean
    VotingSessionRepository repository;

    @Autowired
    WebTestClient client;

    @Test
    public void shouldCreateSession() {
        Agenda agenda = new Agenda("TestAgenda");
        agenda.setId("123");
        VotingSession session = new VotingSession(agenda);

        given(repository.save(BDDMockito.any(VotingSession.class))).willReturn(Mono.just(session));
        given(repository.findByAgendaId("123")).willReturn(Mono.just(session));
        client.post()
                .uri("/session")
                .body(BodyInserters.fromValue(session))
                .exchange()
                .expectStatus().isCreated();
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
    public void shouldReturnBadRequestWhenAgendaAlreadyHasSession() {
        Agenda agenda = new Agenda("123", "TestAgenda", AgendaStatus.OPENED, VoteResult.YES);
        VotingSession session = new VotingSession(agenda);

        given(repository.findByAgendaId("123")).willReturn(Mono.just(session));
        client.post()
                .uri("/session")
                .body(BodyInserters.fromValue(session))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    public void shouldReturnNotFoundWhenSearchingByNonexistentAgenda() {
        given(repository.findByAgendaId("456")).willReturn(Mono.empty());
        client.get()
                .uri("/session/456")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    public void shouldReturnAllSessions() {
        Agenda agendaOne = new Agenda("one");
        Agenda agendaTwo = new Agenda("two");
        Agenda agendaThree = new Agenda("three");

        given(repository.findAll()).willReturn(Flux.just(
                new VotingSession(agendaOne),
                new VotingSession(agendaTwo),
                new VotingSession(agendaThree)
        ));

        client.get()
                .uri("/session")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(VotingSession.class).hasSize(3);
    }

    @Test
    public void shouldReturnCorrectSessionWhenSearchingByAgendaId() {
        Agenda agenda = new Agenda("123", "TestAgenda", AgendaStatus.NEW, null);
        VotingSession session = new VotingSession("345",
                        LocalDateTime.now(),
                        LocalDateTime.now().plusHours(1),
                        agenda,
                        null);
        given(repository.findByAgendaId("123")).willReturn(Mono.just(session));

        client.get()
                .uri("/session/123")
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

        given(repository.findByAgendaId("123")).willReturn(Mono.just(session));
        given(repository.save(BDDMockito.any(VotingSession.class))).willReturn(Mono.just(sessionWithVote));
        client.patch()
                .uri("/session/123")
                .body(BodyInserters.fromValue(vote))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.votes").isNotEmpty();
    }

    @Test
    public void shouldReturnBadRequestWhenVoteHasInvalidValue() {
        Agenda agenda = new Agenda("123", "TestAgenda", AgendaStatus.OPENED, null);
        VotingSession session = new VotingSession(agenda);
        session.setEnd(LocalDateTime.now().plusHours(1));
        Vote vote = new Vote(null, "321");

        given(repository.findByAgendaId("123")).willReturn(Mono.just(session));
        client.patch()
                .uri("/session/123")
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

        given(repository.findByAgendaId("123")).willReturn(Mono.just(session));
        client.patch()
                .uri("/session/123")
                .body(BodyInserters.fromValue(vote))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    public void shouldReturnBadRequestWhenTryingToVoteOnNotOpenedSession() {
        Agenda agenda = new Agenda("123", "TestAgenda", AgendaStatus.NEW, null);
        VotingSession session = new VotingSession(agenda);
        session.setEnd(LocalDateTime.now().plusHours(1));
        Vote vote = new Vote(VoteResult.YES, "321");

        given(repository.findByAgendaId("123")).willReturn(Mono.just(session));
        client.patch()
                .uri("/session/123")
                .body(BodyInserters.fromValue(vote))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    public void shouldReturnBadRequestWhenTryingToVoteOnSessionAfterEnd() {
        Agenda agenda = new Agenda("123", "TestAgenda", AgendaStatus.NEW, null);
        VotingSession session = new VotingSession(agenda);
        Vote vote = new Vote(VoteResult.YES, "321");
        session.setEnd(LocalDateTime.now().minusHours(1));

        given(repository.findByAgendaId("123")).willReturn(Mono.just(session));
        client.patch()
                .uri("/session/123")
                .body(BodyInserters.fromValue(vote))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    public void shouldSetAgendaAsClosedWhenTryingToGetAfterEnd() {
        Agenda agenda = new Agenda("123", "TestAgenda", AgendaStatus.OPENED, null);
        VotingSession session = new VotingSession(agenda);
        session.setEnd(LocalDateTime.now().minusHours(1));

        given(repository.findByAgendaId("123")).willReturn(Mono.just(session));
        client.get()
                .uri("/session/123")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.agenda.id").isEqualTo("123")
                .jsonPath("$.agenda.name").isEqualTo("TestAgenda")
                .jsonPath("$.agenda.status").isEqualTo("CLOSED");
    }

    @Test
    public void shouldSetAgendaAsClosedWhenTryingToVoteAfterEnd() {
        Agenda agenda = new Agenda("123", "TestAgenda", AgendaStatus.OPENED, null);
        VotingSession session = new VotingSession(agenda);
        Vote vote = new Vote(VoteResult.YES, "321");
        session.setEnd(LocalDateTime.now().minusHours(1));

        given(repository.findByAgendaId("123")).willReturn(Mono.just(session));
        client.patch()
                .uri("/session/123")
                .body(BodyInserters.fromValue(vote))
                .exchange()
                .expectStatus().isBadRequest();

        client.get()
                .uri("/session/123")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.agenda.status").isEqualTo("CLOSED");

    }

    @Test
    public void shouldCalculateAgendaResultWhenSessionIsClosed() {
        Agenda agenda = new Agenda("123", "TestAgenda", AgendaStatus.OPENED, null);
        VotingSession session = new VotingSession(agenda);
        Vote voteOne = new Vote(VoteResult.YES, "321");
        Vote voteTwo = new Vote(VoteResult.YES, "654");
        Vote voteThree = new Vote(VoteResult.YES, "987");
        Vote voteFour = new Vote(VoteResult.NO, "123");
        Vote voteFive = new Vote(VoteResult.NO, "456");
        session.addVote(voteOne);
        session.addVote(voteTwo);
        session.addVote(voteThree);
        session.addVote(voteFour);
        session.addVote(voteFive);
        session.setEnd(LocalDateTime.now().minusHours(1));

        given(repository.findByAgendaId("123")).willReturn(Mono.just(session));
        client.get()
                .uri("/session/123")
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
