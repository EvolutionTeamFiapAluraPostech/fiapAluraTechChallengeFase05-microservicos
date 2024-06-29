package br.com.fiap.payment.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@MappedSuperclass
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@EntityListeners(AudittingEntityListener.class)
public abstract class BaseEntity implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @EqualsAndHashCode.Include
  protected UUID id;

  @JsonIgnore
  @Builder.Default
  @Column(nullable = false)
  protected Boolean deleted = false;

  @JsonIgnore
  @Version
  @Column(nullable = false)
  protected Long version;

  @CreationTimestamp
  protected LocalDateTime createdAt;

  protected String createdBy;

  @UpdateTimestamp
  protected LocalDateTime updatedAt;

  protected String updatedBy;

  @Override
  public String toString() {
    return "BaseEntity{" +
        "id=" + id +
        '}';
  }
}
