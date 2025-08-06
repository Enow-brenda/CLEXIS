package com.brenda.clexis.clientGatewayService.exceptions;



import com.brenda.clexis.clientGatewayService.model.dto.Unauthorized;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException)
            throws IOException, ServletException {
        ObjectMapper objectMapper = new ObjectMapper();
        Unauthorized unauthorized= new Unauthorized(request.getServletPath(), "Access Denied","Access Denied for this endpoint",403);
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(unauthorized));
    }


}
