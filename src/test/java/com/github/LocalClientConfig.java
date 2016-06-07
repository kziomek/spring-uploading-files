package com.github;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * @author Krzysztof Ziomek
 * @since 07/06/2016.
 */
@Configuration
public class LocalClientConfig {

    public final static String API_BASE_URL = "http://localhost:8080";

    @Bean
    public RestTemplate formRestTemplate() {

        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setBufferRequestBody(false);

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(requestFactory);

        return restTemplate;
    }

}