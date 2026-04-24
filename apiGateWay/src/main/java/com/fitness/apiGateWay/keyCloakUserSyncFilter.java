package com.fitness.apiGateWay;

import com.fitness.User.UserService;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.text.ParseException;

@Component
@Slf4j
@RequiredArgsConstructor
public class keyCloakUserSyncFilter implements WebFilter {

    private final UserService userService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, @NonNull WebFilterChain chain) {
        String userId = exchange.getRequest().getHeaders().getFirst("X-User-ID");
        String token = exchange.getRequest().getHeaders().getFirst("Authorization");
        RegisterRequest registerRequest = gerUserDetails(token);

        if(userId != null){
            String finalUserId = registerRequest.getKeycloakId();
        }
        // Case 1: Missing headers - just pass through
        if (registerRequest.getKeycloakId() == null || token == null) {
            log.debug("Missing X-User-ID or Authorization headers, continuing without sync");
            return chain.filter(exchange);
        }

        // Case 2: Headers present - sync user
        return userService.validateUser(registerRequest.getKeycloakId())
                .flatMap(exists -> {
                    if (!exists) {
                        log.info("First time user {} - creating in database", userId);

                        if(registerRequest != null){
                            return userService.registerUser (registerRequest)
                                 .then(Mono.empty());
                        }else{
                            return Mono.empty();
                        }
                    } else {
                        log.debug("User {} already exists, skipping sync", userId);
                        return Mono.empty();
                    }
                })
                .onErrorResume(error -> {
                    // Don't block the request if user sync fails
                    log.error("User sync failed for {}: {}", userId, error.getMessage());
                    return Mono.empty();
                })
                .then(Mono.defer(() -> {
                    // Continue with the original request (no mutation needed)
                    return chain.filter(exchange);
                }));
    }

    private RegisterRequest gerUserDetails(String token) {
        try {
            //The JWT parser doesn't understand "Bearer " - it expects ONLY the JWT string. that's why we remove the prefix "Bearer"
            String tokenWithoutBearer = token.replace("Bearer ", "");
            //.parse splits the jwt (header ,payload ,signature)
            SignedJWT signedJWT = SignedJWT.parse(tokenWithoutBearer); //still contains header, payload, signature
            RegisterRequest registerRequest = getRegisterRequest(signedJWT);

            return registerRequest;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static @org.jspecify.annotations.NonNull RegisterRequest getRegisterRequest(SignedJWT signedJWT) throws ParseException {
        JWTClaimsSet claimSet = signedJWT.getJWTClaimsSet();

        //store the information from the jwt to registerRequest
        RegisterRequest registerRequest =  new RegisterRequest();
        registerRequest.setEmail(claimSet.getStringClaim("email"));
        registerRequest.setPassword("dummy@123");
        registerRequest.setFirstname(claimSet.getStringClaim("given_name"));
        registerRequest.setLastname(claimSet.getStringClaim("family_name"));
        registerRequest.setKeycloakId(claimSet.getStringClaim("sub"));

        return registerRequest;
    }

}
