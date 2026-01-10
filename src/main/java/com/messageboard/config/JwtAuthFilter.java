package com.messageboard.config;

import com.messageboard.entity.User;
import com.messageboard.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserRepository userService; // Should be a service/repository returning User

    public JwtAuthFilter(JwtUtil jwtUtil, UserRepository userService) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        // 1. Check for Authorization header and Bearer scheme
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // 2. Extract JWT and user email
            jwt = authHeader.substring(7);
            userEmail = jwtUtil.extractEmail(jwt);

            // 3. Check if email is present and user is NOT already authenticated
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // Note: User must have a getAuthorities() method or equivalent for real security
                User userDetails = userService.findByEmail(userEmail).orElse(null);

                // 4. Validate the token and user details
                if (userDetails != null && jwtUtil.validateToken(jwt, userEmail)) {

                    // Create the authentication token using user details and authorities
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities() // IMPORTANT: Use real authorities/roles here!
                            // Using Collections.emptyList() as a temporary placeholder is okay if User implements UserDetails
                            // java.util.Collections.emptyList()
                    );

                    // Set authentication details
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // 5. Set the authenticated user in the Security Context
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                } else if (userDetails != null) {
                    // Token is invalid or expired - return 401
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"error\": \"Token expired or invalid\"}");
                    return;
                }
            }
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            // Token is expired - return 401
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Token expired\"}");
            return;
        } catch (Exception e) {
            // Other JWT errors - return 401
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Invalid token\"}");
            return;
        }

        // Continue the filter chain
        filterChain.doFilter(request, response);
    }
}