package br.com.fiap.product.infrastructure.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TokenService {

  private static final String ISSUER = "API FIAP Products";

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

  public String getPayloadFrom(String tokenJWT) {
    var chunks = tokenJWT.split("\\.");
    var decoder = Base64.getUrlDecoder();
    return new String(decoder.decode(chunks[1]));
  }
}
