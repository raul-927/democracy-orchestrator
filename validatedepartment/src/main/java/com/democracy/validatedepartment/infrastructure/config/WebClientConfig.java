package com.democracy.validatedepartment.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient customWebClient() {
        return WebClient.builder()
                .baseUrl("http://localhost:8082/humanresources/")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultCookie("cookieKey", "cookieValue")
                .build();
    }
}
