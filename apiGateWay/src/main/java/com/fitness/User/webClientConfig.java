package com.fitness.User;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class webClientConfig {

    @Bean
    @LoadBalanced
    //creating the client prototype
    public WebClient.Builder webClientBuilder(){
        return  WebClient.builder(); //it's like a hibernate factory which creates session, it creates webclients // but this lines only creates a prototype load-balancing adds the actual client
    }

    //injecting the service to the prototype client - makes it an actual client
    @Bean
    public WebClient userServiceWebClient (WebClient.Builder webClientBuilder){
    return webClientBuilder
            .baseUrl("http://USER-SERVICE")
            .build();
    }
}
