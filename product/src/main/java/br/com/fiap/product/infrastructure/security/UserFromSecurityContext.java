package br.com.fiap.product.infrastructure.security;

import br.com.fiap.product.domain.entity.User;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class UserFromSecurityContext {

  public User getUser() {
    var authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication.getPrincipal() != null) {
      if (authentication.getPrincipal() instanceof User) {
        return (User) authentication.getPrincipal();
      }
    }
    return null;
  }
}
