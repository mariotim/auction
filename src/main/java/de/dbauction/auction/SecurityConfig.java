package de.dbauction.auction;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.HttpStatusServerEntryPoint;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.security.web.server.authorization.AuthorizationWebFilter;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import reactor.core.publisher.Mono;

@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@Configuration
public class SecurityConfig {
    private final AuthenticationService authenticationService;

    public SecurityConfig(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                                .pathMatchers("/api/**").authenticated()
                        .anyExchange().authenticated()
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(new HttpStatusServerEntryPoint(HttpStatus.UNAUTHORIZED)))
                .authenticationManager(this::authenticate)
                .authenticationManager(authenticationManager())
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                .addFilterAt(authenticationWebFilter(), SecurityWebFiltersOrder.AUTHENTICATION)
//                .addFilterAt(authorizationWebFilter(), SecurityWebFiltersOrder.AUTHORIZATION)
        ;
        return http.build();
    }

    private Mono<Authentication> authenticate(Authentication authentication) {
        System.out.println("test1");
        String authToken = authentication.getCredentials().toString();
        return authenticationService.validateToken(authToken)
                .mapNotNull(isValid -> isValid ? new UsernamePasswordAuthenticationToken(
                        authentication.getPrincipal(),
                        authentication.getCredentials(),
                        AuthorityUtils.createAuthorityList("ROLE_USER")) : null);
    }

    @Bean
    public ServerAuthenticationConverter authenticationConverter() {
        return exchange -> {
            System.out.println("Checking headers for Authorization...");
            return Mono.justOrEmpty(exchange.getRequest().getHeaders().getFirst("Authorization"))
                    .filter(authHeader -> authHeader.startsWith("Bearer "))
                    .map(authHeader -> {
                        String token = authHeader.substring(7);
                        System.out.println("Extracting token: " + token);
                        return new UsernamePasswordAuthenticationToken(token, token, AuthorityUtils.NO_AUTHORITIES);
                    });
        };
    }

    private AuthenticationWebFilter authenticationWebFilter() {
        AuthenticationWebFilter filter = new AuthenticationWebFilter(authenticationManager());
        filter.setServerAuthenticationConverter(authenticationConverter());
        System.out.println("AuthenticationWebFilter configured and added.");
        return filter;
    }

    private ReactiveAuthenticationManager authenticationManager() {
        return authentication -> {
            System.out.println("Authentication Manager called");
            String token = authentication.getCredentials().toString();  // This should now be non-null
            return authenticationService.validateToken(token)
                    .flatMap(isValid -> {
                        System.out.println("Token validation result: " + isValid);
                        if (isValid) {
                            // Potentially decode the token to extract user details and roles
                            return Mono.just(new UsernamePasswordAuthenticationToken(
                                    authentication.getPrincipal(),  // or a derived user object
                                    authentication.getCredentials(),
                                    AuthorityUtils.createAuthorityList("ROLE_USER")));
                        } else {
                            return Mono.empty();
                        }
                    });
        };
    };

}
