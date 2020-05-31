package com.daniinyan.votingmanager.service;

import com.daniinyan.votingmanager.domain.CpfValidation;
import com.daniinyan.votingmanager.exception.InvalidAuthorException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class CpfValidatorService {

    private final WebClient webClient;
    private final String URL_CPF_VALIDATOR = "https://user-info.herokuapp.com/users/";

    public CpfValidatorService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl(URL_CPF_VALIDATOR).build();
    }

    public Mono<CpfValidation> validateCpf(String cpf) {
        return this.webClient.get().uri("/{cpf}", cpf)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, clientResponse ->
                        Mono.error(new InvalidAuthorException("This CPF is invalid.")))
                .bodyToMono(CpfValidation.class);
    }
}
