package com.daniinyan.votingmanager;

import com.daniinyan.votingmanager.domain.Agenda;
import com.daniinyan.votingmanager.domain.AgendaStatus;
import com.daniinyan.votingmanager.domain.VoteValue;
import com.daniinyan.votingmanager.repository.AgendaRepository;
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

import static org.mockito.BDDMockito.given;

@SpringBootTest
@AutoConfigureWebTestClient
public class AgendaControllerTest {

    @MockBean
    AgendaRepository repository;

    @Autowired
    WebTestClient client;

    @Test
    public void shouldCreateAnAgenda() {
        Agenda agenda = new Agenda("123", "TestAgenda", AgendaStatus.NEW, VoteValue.YES);
        given(repository.save(BDDMockito.any(Agenda.class))).willReturn(Mono.just(agenda));

        client.post()
                .uri("/agenda")
                .body(BodyInserters.fromValue(agenda))
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isEqualTo("123")
                .jsonPath("$.name").isEqualTo("TestAgenda")
                .jsonPath("$.status").isEqualTo("NEW")
                .jsonPath("$.result").isEqualTo("YES");
    }

    @Test
    public void shouldReturnBadRequestWhenAgendaDoesNotHaveAName() {
        Agenda agenda = new Agenda();
        client.post()
                .uri("/agenda")
                .body(BodyInserters.fromValue(agenda))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    public void shouldReturnCorrectAgendaWhenSearchingById() {
        Agenda agenda = new Agenda("123", "TestAgenda", AgendaStatus.OPENED, VoteValue.YES);
        given(repository.findById("123")).willReturn(Mono.just(agenda));

        client.get()
                .uri("/agenda/123")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo("123")
                .jsonPath("$.name").isEqualTo("TestAgenda")
                .jsonPath("$.result").isEqualTo("YES");
    }

    @Test
    public void shouldReturnNotFoundWhenSearchingByNonexistentId() {
        given(repository.findById("456")).willReturn(Mono.empty());
        client.get()
                .uri("/agenda/456")
                .exchange()
                .expectStatus().isNotFound();

    }

    @Test
    public void shouldReturnAllAgendas() {
        given(repository.findAll()).willReturn(Flux.just(
                new Agenda("agendaOne"),
                new Agenda("agendaTwo"),
                new Agenda("agendaThree")
        ));

        client.get()
                .uri("/agenda")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Agenda.class).hasSize(3);
    }
}