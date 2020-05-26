package com.daniinyan.votingmanager.api.v1;

import com.daniinyan.votingmanager.exception.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionController {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionController.class);

    @ExceptionHandler(IdNotFoundException.class)
    public ResponseEntity<String> handleNotFound(IdNotFoundException ex) {
        logger.info(ex.getMessage(), ex);
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(RequiredNameException.class)
    public ResponseEntity<String> handleRequiredName(RequiredNameException ex) {
        logger.info(ex.getMessage(), ex);
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RequiredAgendaException.class)
    public ResponseEntity<String> handleRequiredAgenda(RequiredAgendaException ex) {
        logger.info(ex.getMessage(), ex);
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(OpenedSessionException.class)
    public ResponseEntity<String> handleOpenedSession(OpenedSessionException ex) {
        logger.info(ex.getMessage(), ex);
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AgendaException.class)
    public ResponseEntity<String> handleAgenda(AgendaException ex) {
        logger.info(ex.getMessage(), ex);
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidVoteException.class)
    public ResponseEntity<String> handleInvalidVote(InvalidVoteException ex) {
        logger.info(ex.getMessage(), ex);
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidAuthorException.class)
    public ResponseEntity<String> handleInvalidAuthor(InvalidAuthorException ex) {
        logger.info(ex.getMessage(), ex);
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }


}
