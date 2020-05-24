package com.daniinyan.votingmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.reactive.config.EnableWebFlux;

@SpringBootApplication
@EnableWebFlux
public class VotingManagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(VotingManagerApplication.class, args);
	}

}
