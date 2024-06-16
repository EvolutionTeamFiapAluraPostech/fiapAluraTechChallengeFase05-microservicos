package br.com.fiap.company.infrastructure.security;

import static org.flywaydb.core.internal.util.JsonUtils.getFromJson;

import br.com.fiap.company.domain.entity.User;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

@Service
public class TokenService {

  private static final String ISSUER = "API FIAP Companies";

  @Value("${api.security.token.secret}")
  private String secret;

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

  public User getUserFrom(String token) {
    var payload = this.getPayloadFrom(token);
    var id = getFromJson(payload, "id");
    var name = getFromJson(payload, "name");
    var sub = getFromJson(payload, "sub");
    var iss = getFromJson(payload, "iss");
    var exp = Long.valueOf(getFromJson(payload, "exp"));
    var authorities = getAuthorities(payload);
    return new User(id, name, sub, iss, exp, authorities);
  }

  private List<GrantedAuthority> getAuthorities(String payload) {
    var authorities = getFromJson(payload, "authorities");
    if (authorities != null && !authorities.trim().isEmpty()) {
      return Collections.singletonList(new SimpleGrantedAuthority(authorities));
    }
    return Collections.emptyList();
  }

  private String getPayloadFrom(String tokenJWT) {
    var chunks = tokenJWT.split("\\.");
    var decoder = Base64.getUrlDecoder();
    return new String(decoder.decode(chunks[1]));
  }
}
