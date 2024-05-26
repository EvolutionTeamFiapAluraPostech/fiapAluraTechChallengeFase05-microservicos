package br.com.users.user.presentation.api;

import br.com.users.user.infrastructure.security.TokenService;
import br.com.users.user.domain.entity.User;
import br.com.users.user.presentation.dto.AuthenticateInputDto;
import br.com.users.user.presentation.dto.TokenJwtInput;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/authenticate")
public class AuthenticationController implements AuthenticationInterface {

  private final AuthenticationManager authenticationManager;
  private final TokenService tokenService;

  public AuthenticationController(
      AuthenticationManager authenticationManager,
      TokenService tokenService
  ) {
    this.authenticationManager = authenticationManager;
    this.tokenService = tokenService;
  }

  @PostMapping
  public ResponseEntity<?> authenticate(
      @RequestBody @Valid AuthenticateInputDto authenticateInputDto) {
    var authenticationToken = new UsernamePasswordAuthenticationToken(authenticateInputDto.email(),
        authenticateInputDto.password());
    var authenticate = authenticationManager.authenticate(authenticationToken);
    var user = ((User) authenticate.getPrincipal());
    var tokenJwt = tokenService.generateToken(user);
    return ResponseEntity.ok(new TokenJwtInput(tokenJwt));
  }
}
