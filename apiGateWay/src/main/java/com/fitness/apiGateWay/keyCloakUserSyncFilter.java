package com.fitness.apiGateWay;

import com.fitness.User.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@RequiredArgsConstructor
public class keyCloakUserSyncFilter implements WebFilter {

    private final UserService userService;
    @Override
    //ServerWebExchange - contains the requests and response
    public Mono<Void> filter(ServerWebExchange exchange , @NonNull WebFilterChain chain){
        String userId = exchange.getRequest().getHeaders().getFirst("X-User-ID");
        String token = exchange.getRequest().getHeaders().getFirst("Authorization");

        if(userId !=null && token!=null){
            return userService.validateUser(userId)
                    .flatMap(exist ->{
                        if(!exist){
                            // register User

                        }else{
                            log.info("User already exists, skipping sync");
                            return Mono.empty();
                        }
                    })
                    .then(Mono.defer(()->{
                        ServerHttpRequest mutatedRequest = (ServerHttpRequest) exchange.getRequest().mutate()
                                .header("X-User-ID" , userId)
                                .build();
                        return chain.filter(exchange.mutate().request(mutatedRequest).build());
                    }));
        }

    }
}
