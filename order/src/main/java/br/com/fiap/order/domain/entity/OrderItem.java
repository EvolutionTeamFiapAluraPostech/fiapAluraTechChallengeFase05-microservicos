package br.com.fiap.order.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(schema = "order_management", name = "order_items")
public class OrderItem extends BaseEntity {

  @JsonIgnore
  @ManyToOne
  @JoinColumn(name = "order_id")
  private Order order;
  private UUID productId;
  private String productSku;
  private BigDecimal quantity;
  private BigDecimal price;
  @JsonIgnore
  private BigDecimal totalAmount;

  public void calculateTotalItemAmount() {
    totalAmount = quantity.multiply(price);
  }
}
