package br.com.fiap.payment.domain.entity;

public interface AuditableEntity {

  void setCreatedBy(String email);
  void setUpdatedBy(String email);
}
