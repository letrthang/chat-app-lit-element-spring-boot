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
    // Access token: 15 minutes
    private final long JWT_TOKEN_VALIDITY_MS = 15 * 60 * 1000;
    // Refresh token: 7 days
    private final long REFRESH_TOKEN_VALIDITY_MS = 7 * 24 * 60 * 60 * 1000L;

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

    /**
     * Builds signed JWT with subject, timestamps, and expiration
     */
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

    /**
     * Generates a refresh token with longer expiration
     */
    public String generateRefreshToken(String email) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "refresh");
        return createRefreshToken(claims, email);
    }

    /**
     * Creates a refresh token with extended validity
     */
    private String createRefreshToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_VALIDITY_MS))
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact();
    }

    /**
     * Validates refresh token
     */
    public Boolean validateRefreshToken(String token, String userEmail) {
        try {
            final String email = extractEmail(token);
            final Claims claims = extractAllClaims(token);
            String tokenType = (String) claims.get("type");
            return (email.equals(userEmail) && !isTokenExpired(token) && "refresh".equals(tokenType));
        } catch (Exception e) {
            System.err.println("Refresh token validation failed: " + e.getMessage());
            return false;
        }
    }
}