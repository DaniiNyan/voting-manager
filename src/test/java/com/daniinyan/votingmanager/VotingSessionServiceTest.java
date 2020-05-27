package com.daniinyan.votingmanager;

import com.daniinyan.votingmanager.domain.Agenda;
import com.daniinyan.votingmanager.domain.AgendaStatus;
import com.daniinyan.votingmanager.domain.VoteResult;
import com.daniinyan.votingmanager.domain.VotingSession;
import com.daniinyan.votingmanager.exception.VotingSessionException;
import com.daniinyan.votingmanager.exception.RequiredAgendaException;
import com.daniinyan.votingmanager.service.VotingSessionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class VotingSessionServiceTest {

    @Autowired
    VotingSessionService service;

    @Test
    public void shouldThrowRequiredAgendaExceptionWhenValidatingSessionWithoutAgenda() {
        assertThrows(RequiredAgendaException.class, () -> {
            VotingSession session = new VotingSession();
            service.validateSessionToOpen(session);
        });
    }

    @Test
    public void shouldThrowOpenedSessionExceptionWhenValidatingAlreadyOpenedSession() {
        assertThrows(VotingSessionException.class, () -> {
            Agenda agenda = new Agenda("123", "Test", AgendaStatus.OPENED, null);
            VotingSession session = new VotingSession(agenda);
            service.validateSessionToOpen(session);
        });
    }

    @Test
    public void shouldSetDefaultValuesWhenCreatingSession() {
        Agenda agenda = new Agenda("TestAgenda");
        VotingSession session = new VotingSession(agenda);

        VotingSession actualSession = service.setDefaultValues(session);

        assertEquals(AgendaStatus.OPENED, actualSession.getAgenda().getStatus());
        assertEquals(VoteResult.EMPTY, actualSession.getAgenda().getResult());
    }
}
