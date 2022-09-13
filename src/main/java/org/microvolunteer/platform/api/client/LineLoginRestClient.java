package org.microvolunteer.platform.api.client;

import org.microvolunteer.platform.domain.resource.request.LineLoginRequest;
import org.microvolunteer.platform.domain.resource.request.LineTokenRequest;
import org.microvolunteer.platform.domain.resource.response.LineTokenResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
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

    @Value("${line-login.line_token_url}")
    private String line_token_url;

    @Value("${line-login.line_login_url}")
    private String line_login_url;

    @Value("${line-login.login_redirect_uri}")
    private String login_redirect_uri;

    @Value("${line-login.oauth_redirect_uri}")
    private String oauth_redirect_uri;

    public String lineAuth(String code) {
        RestTemplate restTemplate = new RestTemplate();

        //String line_token_url = "https://api.line.me/oauth2/v2.1/token";
        //String redirect_uri = "http://127.0.0.1:8080/v1/api/auth";

        LineTokenRequest lineTokenRequest = LineTokenRequest.builder()
                .grant_type("authorization_code")
                .client_id(client_id)
                .client_secret(client_secret)
                .redirect_uri(oauth_redirect_uri)
                .build();

        try {
            RequestEntity<LineTokenRequest> request = RequestEntity
                    .post(line_token_url)
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

    public void lineLogin() {
        RestTemplate restTemplate = new RestTemplate();
        LineLoginRequest lineLoginRequest = LineLoginRequest.builder()
                .response_type("code")
                .redirect_uri(login_redirect_uri)
                .client_id(client_id)
                .build();
        try {
            RequestEntity<LineLoginRequest> request = RequestEntity
                    .post(line_login_url)
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(lineLoginRequest);
            //restTemplate.exchange(request, LineTokenResponse.class);
            String response_type = "code";
            String state="191001000";
            String scope= "openid%20profile";
            //String scope="openid profile";
            //String scope="openid%20profile";
            ResponseEntity<String> response = restTemplate.exchange(line_login_url,
                    HttpMethod.GET,
                    null,
                    String.class,
                    response_type,client_id,login_redirect_uri,response_type,state,scope);
            logger.info("RestClient response : {}", response);
        } catch (RestClientException e) {
            logger.info("RestClient error : {}", e.toString());
        }
    }
}
