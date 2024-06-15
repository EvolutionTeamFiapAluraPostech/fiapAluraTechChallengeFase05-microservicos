package br.com.fiap.order.infrastructure.security;

import br.com.fiap.order.domain.entity.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class SecurityFilter extends OncePerRequestFilter {

  private final TokenService tokenService;

  public SecurityFilter(TokenService tokenService) {
    this.tokenService = tokenService;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    var tokenJwt = getToken(request);
    if (tokenJwt != null) {
      var payload = tokenService.getPayloadFrom(tokenJwt);
      var user = getUserFrom(payload);
      var authenticationToken = new UsernamePasswordAuthenticationToken(user, null, null);
      SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }
    filterChain.doFilter(request, response);
  }

  private User getUserFrom(String payload) throws JsonProcessingException {
    var objectMapper = new ObjectMapper();
    return objectMapper.readValue(payload, User.class);
  }

  private String getToken(HttpServletRequest request) {
    var authorizationHeader = request.getHeader("Authorization");
    if (authorizationHeader != null) {
      return authorizationHeader.replace("Bearer ", "").trim();
    }
    return null;
  }
}
