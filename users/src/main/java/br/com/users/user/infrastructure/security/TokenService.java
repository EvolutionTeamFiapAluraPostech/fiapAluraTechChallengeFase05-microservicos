package br.com.users.user.infrastructure.security;

import br.com.users.user.domain.entity.User;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TokenService {

  private static final String ISSUER = "API FIAP Users";

  @Value("${api.security.token.secret}")
  private String secret;

  public String generateToken(User user) {
    try {
      var algorithm = Algorithm.HMAC256(secret);
      var authority = getAuthorityFrom(user);
      return JWT.create()
          .withIssuer(ISSUER)
          .withSubject(user.getEmail())
          .withClaim("id", user.getId().toString())
          .withClaim("name", user.getName())
          .withClaim("authorities", authority)
          .withExpiresAt(getExpirationDate())
          .sign(algorithm);
    } catch (JWTCreationException exception) {
      throw new RuntimeException("Erro ao gerar o token JWT", exception);
    }
  }

  private String getAuthorityFrom(User user) {
    var authority = "%s";
    if (user.getAuthorities() != null && !user.getAuthorities().isEmpty()) {
      return authority.formatted(user.getAuthorities().get(0).getName());
    }
    return "";
  }

  public String getSubject(String tokenJWT) {
    try {
      var algorithm = Algorithm.HMAC256(this.secret);
      return JWT.require(algorithm)
          .withIssuer(ISSUER)
          .build()
          .verify(tokenJWT)
          .getSubject();
    } catch (JWTVerificationException exception) {
      throw new RuntimeException("Token JWT inválido ou expirado!", exception);
    }
  }

  private Instant getExpirationDate() {
    return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
  }
}
