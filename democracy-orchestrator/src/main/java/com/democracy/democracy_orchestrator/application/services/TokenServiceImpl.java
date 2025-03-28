package com.democracy.democracy_orchestrator.application.services;



import com.democracy.democracy_orchestrator.domain.models.KeyCloakToken;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

@Service
public class TokenServiceImpl implements TokenService{


    @Override
    public String obtainToken(){
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("client_id","democracy_client");
        map.add("client_secret","YYcdVQO1lB9F5IjxjN6ljHueBWhZz1aZ");
        map.add("grant_type","password");
        map.add("username","raraherher9274");
        map.add("password","raraherher9274");
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);
        ResponseEntity<KeyCloakToken> response =
                restTemplate.exchange("http://localhost:8181/realms/democracy_realm/protocol/openid-connect/token",
                        HttpMethod.POST,
                        entity,
                        KeyCloakToken.class);

        return Objects.requireNonNull(response.getBody()).getToken_type() + " "+response.getBody().getAccess_token();
    }
}
