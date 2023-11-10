package com.ex.service;

import com.ex.model.CreateCustomer;
import com.ex.model.Customer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
public class ApiService {
    @Autowired
    RestTemplate restTemplate;

    public List<Customer> getCustomerList(HttpSession session) {
        String token = (String) session.getAttribute("token");
        String apiUrl = "https://qa2.sunbasedata.com/sunbase/portal/api/assignment.jsp?cmd=get_customer_list";

        // Set up headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Set up WebClient
        WebClient client = WebClient.builder()
                .baseUrl(apiUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .build();

        // Make the GET request
        String response = client.get()
                .retrieve()
                .bodyToMono(String.class)
                .block();

        ObjectMapper mapper = new ObjectMapper();

        List<Customer> customerList = null;
        try {
            customerList = mapper.readValue(response,
                    new TypeReference<List<Customer>>() {
                    });
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return customerList;
    }

    public boolean delete(HttpSession session, String uuid) {
        String token = (String) session.getAttribute("token");
        String trimuuid = uuid.trim();
        String apiUrl = "https://qa2.sunbasedata.com/sunbase/portal/api/assignment.jsp?cmd=delete&uuid=" + trimuuid;
        System.out.println("delete url " + apiUrl);
        System.out.println("uuid" + uuid + "\ntrimuuid" + trimuuid);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Set up WebClient
        WebClient client = WebClient.builder()
                .baseUrl(apiUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .build();

        // Make the GET request
        String response = client.post()
                .retrieve()
                .bodyToMono(String.class)
                .block();
        String trim = response.trim();
        System.out.println("response: ++++++++++++  " + trim + "+++");
        return true;
    }


    public boolean add(HttpSession session, CreateCustomer customer) {
        String token = (String) session.getAttribute("token");
        String apiUrl = "https://qa2.sunbasedata.com/sunbase/portal/api/assignment.jsp?cmd=create";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Set up WebClient
        WebClient client = WebClient.builder()
                .baseUrl(apiUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE) // Specify content type
                .build();

        // Convert object to JSON
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String json = objectMapper.writeValueAsString(customer);
//            System.out.println("token =  " + token);
//            System.out.println("cusomer  " + customer);
//            System.out.println("json " + json);

            // Make the POST request
            String response = client.post()
                    .bodyValue(json)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            String trim = response.trim();
            System.out.println("Response: " + trim);
            return trim != null && trim.equals("Successfully Created"); // Adjust based on your actual API response

        } catch (JsonProcessingException e) {
            e.printStackTrace(); // Log the exception for debugging
            return false;
        }
    }


    public boolean update(HttpSession session, String uuid, CreateCustomer customer) {

        String token = (String) session.getAttribute("token");
        String apiUrl = "https://qa2.sunbasedata.com/sunbase/portal/api/assignment.jsp?cmd=update";


        // Set up WebClient
        WebClient client = WebClient.builder()
                .baseUrl(apiUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE) // Specify content type
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String json = objectMapper.writeValueAsString(customer);


            // Make the POST request
            String response = client.post()
                    .uri(uriBuilder -> uriBuilder
                            .queryParam("uuid", uuid)
                            .build())
                    .bodyValue(json)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            String trim = response.trim();
            System.out.println("Response: " + trim);
            return trim != null && trim.equals("Successfully Updated"); // Adjust based on your actual API response
        } catch (JsonProcessingException e) {
            System.out.println("error occurred in update");
            return  false ;
        }
    }
}
