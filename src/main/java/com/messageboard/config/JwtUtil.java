package com.messageboard.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value; // New import for @Value
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    // Inject secret string from application.properties
    @Value("${jwt.secret}")
    private String secretString;

    private SecretKey secretKey;
    // 5 hours in seconds * 1000 ms/sec = 18,000,000 milliseconds
    private final long JWT_TOKEN_VALIDITY_MS = 5 * 60 * 60 * 1000;

    @PostConstruct
    public void init() {
        // Initialize the SecretKey from the configured string (safe, persistent key)
        this.secretKey = Keys.hmacShaKeyFor(secretString.getBytes());
    }

    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        // Using Jwts.parser() which replaces Jwts.parserBuilder() in the latest API
        return Jwts.parser()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Boolean isTokenExpired(String token) {
        // Add error handling here if token is invalid before extracting
        try {
            return extractExpiration(token).before(new Date());
        } catch (Exception e) {
            // Token is likely invalid or malformed if extraction fails
            return true;
        }
    }

    public String generateToken(String email) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, email);
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                // Use the correct millisecond constant
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY_MS))
                // Use the modern signature algorithm style
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact();
    }

    public Boolean validateToken(String token, String userEmail) {
        try {
            final String email = extractEmail(token);
            return (email.equals(userEmail) && !isTokenExpired(token));
        } catch (Exception e) {
            // Log the exception (e.g., ExpiredJwtException, SignatureException)
            System.err.println("JWT Validation failed: " + e.getMessage());
            return false;
        }
    }
}