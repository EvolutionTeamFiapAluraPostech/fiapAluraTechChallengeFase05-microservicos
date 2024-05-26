package br.com.users.user.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;

@MappedSuperclass
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@EntityListeners(AudittingEntityListener.class)
@Where(clause = "deleted = false")
public abstract class BaseEntity implements Serializable, AuditableEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @EqualsAndHashCode.Include
  protected UUID id;

  @Builder.Default
  @Column(nullable = false)
  protected Boolean deleted = false;

  @Version
  @Column(nullable = false)
  protected Long version;

  @CreationTimestamp
  protected LocalDateTime createdAt;

  protected String createdBy;

  @UpdateTimestamp
  protected LocalDateTime updatedAt;

  protected String updatedBy;
}