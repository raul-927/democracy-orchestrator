package com.democracy.democracy_orchestrator.application.services;



import com.democracy.democracy_orchestrator.domain.models.KeyCloakToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

@Service
public class TokenServiceImpl implements TokenService{

    //@Value("${client-id}")
    private final String clientId = "democracy_client";

    //@Value("${client-secret}")
    private final String clientSecret = "YYcdVQO1lB9F5IjxjN6ljHueBWhZz1aZ";

    //@Value("${gran-type}")
    private final String granType = "password";

    //@Value("${user-name}")
    private final String userName ="raraherher9274";

    //@Value("${password}")
    private final String password ="raraherher9274";

    //@Value("${url}")
    private final String url = "http://localhost:8181/realms/democracy_realm/protocol/openid-connect/token";

    //@Override
    public String obtainToken(){
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
