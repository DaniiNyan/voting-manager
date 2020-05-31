package com.daniinyan.votingmanager.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                title = "Voting Manager API",
                version = "v1",
                description = "This app provides REST APIs to manage voting sessions",
                contact = @Contact(
                        name = "Daniela Araujo",
                        email = "daniela.dbass@gmail.com",
                        url = "https://github.com/DaniiNyan/"
                )
        ),
        servers = @Server(
                url = "http://localhost:8080/",
                description = "Demo"
        )
)
public class OpenApiConfig {
}
