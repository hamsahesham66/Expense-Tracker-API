package com.expensetracker.expense_tracker.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private  final  UserDetailsService userDetailsService;
    @Override
    protected void doFilterInternal(
          @NonNull HttpServletRequest request,
          @NonNull       HttpServletResponse response,
          @NonNull  FilterChain filterChain) throws ServletException, IOException {
        try {
            final String authHeader = request.getHeader("Authorization");
            final String jwt;
            final String userEmail;


            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }
            jwt = authHeader.substring(7);
            userEmail = jwtService.extractUserEmail(jwt);
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
                if (jwtService.isTokenValid(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken
                            (userDetails, null, userDetails.getAuthorities());
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }
            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException ex) {
            // 🚨 CATCH EXPIRED TOKEN HERE
            handleException(response, "Token has expired. Please log in again.", HttpServletResponse.SC_UNAUTHORIZED);
        } catch (JwtException | IllegalArgumentException ex) {
            // 🚨 CATCH OTHER JWT ERRORS (Malformed, Empty, etc.)
            handleException(response, "Invalid Token", HttpServletResponse.SC_UNAUTHORIZED);
        } catch (Exception ex) {
            // 🚨 CATCH UNEXPECTED ERRORS
            handleException(response, "Authentication error: " + ex.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
        private void handleException(HttpServletResponse response, String message, int status) throws IOException {
            response.setStatus(status);
            response.setContentType("application/json");
            Map<String, Object> errorDetails = new HashMap<>();
            errorDetails.put("status", status);
            errorDetails.put("message", message);
            errorDetails.put("timestamp", LocalDateTime.now().toString());

            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(response.getWriter(), errorDetails);
    }
}
