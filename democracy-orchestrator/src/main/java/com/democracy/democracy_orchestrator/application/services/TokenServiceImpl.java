package com.democracy.democracy_orchestrator.application.services;



import com.democracy.democracy_orchestrator.domain.models.KeyCloakToken;
import com.democracy.democracy_orchestrator.domain.models.Profession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Objects;

import static com.democracy.democracy_orchestrator.infrastructure.config.UrlConstant.*;
import static com.democracy.democracy_orchestrator.infrastructure.config.UrlConstant.SELECT;

@Service
public class TokenServiceImpl implements TokenService{

    @Autowired
    private WebClient webClient;

    @Value("${keycloak.client-id}")
    private String clientId;

    @Value("${keycloak.client-secret}")
    private  String clientSecret;

    @Value("${keycloak.gran-type}")
    private  String granType;

    @Value("${keycloak.user-name}")
    private  String userName;

    @Value("${keycloak.password}")
    private  String password;

    @Value("${keycloak.url}")
    private  String url;

    @Override
    public String obtainToken(){

        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("client_id",clientId);
        queryParams.add("client_secret",clientSecret);
        queryParams.add("grant_type",granType);
        queryParams.add("username",userName);
        queryParams.add("password",password);; // Adding multiple values for the same key

        webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path(url)
                        .queryParams(queryParams)
                        .build())
                .headers((headers) -> headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED))
                .retrieve()
                .bodyToMono(KeyCloakToken.class)
                .map(tk ->{
                    return tk.getToken_type() + " "+tk.getAccess_token();
                })
                .subscribe();



        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("client_id",clientId);
        map.add("client_secret",clientSecret);
        map.add("grant_type",granType);
        map.add("username",userName);
        map.add("password",password);
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);
        ResponseEntity<KeyCloakToken> response =
                restTemplate.exchange(url,
                        HttpMethod.POST,
                        entity,
                        KeyCloakToken.class);

        return Objects.requireNonNull(response.getBody()).getToken_type() + " "+response.getBody().getAccess_token();
    }
}
