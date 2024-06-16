package br.com.fiap.company.domain.entity;

public interface AuditableEntity {

  void setCreatedBy(String email);
  void setUpdatedBy(String email);
}
