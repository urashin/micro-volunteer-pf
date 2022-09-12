package org.microvolunteer.platform.api.client;

import org.microvolunteer.platform.domain.resource.request.LineTokenRequest;
import org.microvolunteer.platform.domain.resource.response.LineTokenResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
public class LineLoginRestClient {
    private Logger logger = LoggerFactory.getLogger(LineLoginRestClient.class);

    @Value("${line-login.client_id}")
    private String client_id;

    @Value("${line-login.client_secret}")
    private String client_secret;

    public String lineAuth(String code) {
        RestTemplate restTemplate = new RestTemplate();

        String get_token_url = "https://api.line.me/oauth2/v2.1/token";
        String redirect_uri = "http://127.0.0.1:8080/v1/api/auth";

        LineTokenRequest lineTokenRequest = LineTokenRequest.builder()
                .grant_type("authorization_code")
                .client_id(client_id)
                .client_secret(client_secret)
                .redirect_uri(redirect_uri)
                .build();

        try {
            RequestEntity<LineTokenRequest> request = RequestEntity
                    .post(get_token_url)
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(lineTokenRequest);
            ResponseEntity<LineTokenResponse> response = restTemplate.exchange(request, LineTokenResponse.class);
            String id_token = response.getBody().getId_token();
            return id_token;

        } catch (RestClientException e) {
            logger.info("RestClient error : {}", e.toString());
            return "";
        }
    }
}
