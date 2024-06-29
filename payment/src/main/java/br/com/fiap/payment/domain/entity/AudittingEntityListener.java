package br.com.fiap.payment.domain.entity;

import br.com.fiap.payment.infrastructure.security.UserFromSecurityContext;
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
      ((BaseEntity) entity).setCreatedBy(userFromSecurityContext.getUser().getSub());
    }
  }

  @PreUpdate
  public void onPreUpdate(Object entity) {
    if (userFromSecurityContext.getUser() != null) {
      ((BaseEntity) entity).setUpdatedBy(userFromSecurityContext.getUser().getSub());
    }
  }
}
