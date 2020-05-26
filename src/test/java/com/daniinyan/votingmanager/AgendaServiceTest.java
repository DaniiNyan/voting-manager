package com.daniinyan.votingmanager;

import com.daniinyan.votingmanager.domain.Agenda;
import com.daniinyan.votingmanager.domain.AgendaStatus;
import com.daniinyan.votingmanager.domain.VoteValue;
import com.daniinyan.votingmanager.exception.RequiredNameException;
import com.daniinyan.votingmanager.repository.AgendaRepository;
import com.daniinyan.votingmanager.service.AgendaService;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@SpringBootTest
public class AgendaServiceTest {

    @MockBean
    AgendaRepository repository;

    @Autowired
    AgendaService service;

    @Test
    public void shouldReturnCorrectMonoOnCreate() {
        Agenda agenda = new Agenda("Test");
        given(repository.save(BDDMockito.any(Agenda.class))).willReturn(Mono.just(agenda));

        service.save(agenda).subscribe(createdAgenda ->
            assertEquals(agenda.getName(), createdAgenda.getName()));
    }

    @Test
    public void shouldThrowExceptionWhenAgendaDoesNotHaveName() {
        Agenda agenda = new Agenda();
        agenda.setResult(VoteValue.YES);
        assertThrows(RequiredNameException.class, () -> service.save(agenda));
    }

    @Test
    public void shouldReturnCorrectAgendaById() {
        Agenda agenda = new Agenda("123", "TestAgenda", AgendaStatus.OPENED, VoteValue.YES);
        given(repository.findById("123")).willReturn(Mono.just(agenda));

        service.findById("123").subscribe(foundAgenda -> {
                assertEquals(agenda.getId(), foundAgenda.getId());
                assertEquals(agenda.getName(), foundAgenda.getName());
                assertEquals(agenda.getStatus(), foundAgenda.getStatus());
                assertEquals(agenda.getResult(), foundAgenda.getResult());
        });
    }
}
