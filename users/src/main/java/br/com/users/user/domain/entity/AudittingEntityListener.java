package br.com.users.user.domain.entity;

import br.com.users.user.infrastructure.security.UserFromSecurityContext;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import org.springframework.stereotype.Component;

@Component
public class AudittingEntityListener {

  private final UserFromSecurityContext userFromSecurityContext;

  public AudittingEntityListener(UserFromSecurityContext userFromSecurityContext) {
    this.userFromSecurityContext = userFromSecurityContext;
  }

  @PrePersist
  public void onPrePersist(Object entity) {
    if (userFromSecurityContext.getUser() != null) {
      ((BaseEntity) entity).setCreatedBy(userFromSecurityContext.getUser().getEmail());
    }
  }

  @PreUpdate
  public void onPreUpdate(Object entity) {
    if (userFromSecurityContext.getUser() != null) {
      ((BaseEntity) entity).setUpdatedBy(userFromSecurityContext.getUser().getEmail());
    }
  }
}
