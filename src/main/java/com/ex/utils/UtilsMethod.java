package com.ex.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class UtilsMethod {

    @Autowired
    RestTemplate restTemplate;

    //    ...................properties .............
    @Value("${user.userid}")
    private String userid;
    @Value("${user.password}")
    private String password;

    @Value("${user.authUrl}")
    private String authUrl;


    public static  String token = "";
    public boolean verify(String userid, String password) {
        if (userid.equals(this.userid) && password.equals(this.password)) {
            return true;
        } else {
            return false;
        }
    }

    public String generateToken(String userid, String password) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String jsonBody = String.format("{\"login_id\": \"%s\", \"password\": \"%s\"}", userid, password);
        HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);
        String response = restTemplate.postForObject(authUrl, entity, String.class);
        token = extractToken(response);
        System.out.println("Access Token: "+token);
        return token;
    }

  private   String extractToken(String response) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode jsonNode = mapper.readTree(response);
            token = jsonNode.get("access_token").asText();

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return token;
    }


}
