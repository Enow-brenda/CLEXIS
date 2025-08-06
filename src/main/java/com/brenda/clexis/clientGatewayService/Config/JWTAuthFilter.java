package com.brenda.clexis.clientGatewayService.Config;


import com.brenda.clexis.clientGatewayService.model.dto.TokenDetails;
import com.brenda.clexis.clientGatewayService.service.implementations.OurUserDetailsService;
import com.brenda.clexis.clientGatewayService.utils.JWTUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JWTAuthFilter extends OncePerRequestFilter {

    private final JWTUtils jwtUtils;
    private final OurUserDetailsService ourUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
       final String authHeader =  request.getHeader("Authorization");
       final String jwtToken;
//       final String userId;
//       final String role;
//       final String email;
       final String username;

       if(authHeader == null || authHeader.isBlank()){
           filterChain.doFilter(request, response);
           return;
       }
       jwtToken = authHeader.substring(7);
//       userId = jwtUtils.extractUserId(jwtToken);
//        role = jwtUtils.extractRole(jwtToken);
//        email = jwtUtils.extractEmail(jwtToken);
        username = jwtUtils.extractUsername(jwtToken);
//        var tokenDetails = TokenDetails.builder()
//                .userId(userId)
//                .role(role)
//                .email(email)
//                .username(username)
//                .build();

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = ourUserDetailsService.loadUserByUsername(username);
            if (jwtUtils.isTokenValid(jwtToken, userDetails)) {
                SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
                UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );
                token.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                securityContext.setAuthentication(token);
                SecurityContextHolder.setContext(securityContext);
            }
        }

        filterChain.doFilter(request, response);
    }
}
