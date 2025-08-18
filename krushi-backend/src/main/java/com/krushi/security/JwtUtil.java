package com.krushi.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {
    private final Key key;
    private final long validityMs = 1000L * 60 * 60 * 24; // 24h

    public JwtUtil() {
        // read secret from env var or fallback (set strong secret in production)
        String secret = System.getenv().getOrDefault("JWT_SECRET", "krushi-dev-secret-please-change-very-long");
        key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateToken(String username) {
        var now = new Date();
        var exp = new Date(now.getTime() + validityMs);
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(key)
                .compact();
    }

    public String extractUsername(String token) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build()
                    .parseClaimsJws(token).getBody().getSubject();
        } catch (JwtException e) {
            return null;
        }
    }
}
