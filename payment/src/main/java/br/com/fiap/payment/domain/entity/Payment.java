package br.com.fiap.payment.domain.entity;

import br.com.fiap.payment.domain.enums.PaymentStatus;
import br.com.fiap.payment.domain.enums.PaymentType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;

@SuperBuilder
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(schema = "payment_management", name = "payments")
public class Payment extends BaseEntity {

  @JsonIgnore
  @Builder.Default
  private Boolean active = true;
  private UUID orderId;
  private UUID companyId;
  private String companyName;
  private UUID customerId;
  private String customerName;
  @Enumerated(EnumType.STRING)
  private PaymentType paymentType;
  @Enumerated(EnumType.STRING)
  private PaymentStatus paymentStatus;
  @CreationTimestamp
  private LocalDateTime paymentDate;
  private BigDecimal paymentTotalAmount;

  public boolean isPaymentSaved() {
    return getId() != null;
  }
}
