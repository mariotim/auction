package de.dbauction.auction;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.security.Key;
@Service
public class AuthenticationService {
    private final Key key;
    public AuthenticationService(@Value("${jwt.secret}")  String secretKey) {
        byte[] keyBytes = java.util.Base64.getDecoder().decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public Mono<Boolean> validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return Mono.just(true);
        } catch (Exception e) {
            return Mono.just(false);
        }
    }

    public String extractUserId(String token) {
        return Jwts.parser()
                .setSigningKey(key).build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
