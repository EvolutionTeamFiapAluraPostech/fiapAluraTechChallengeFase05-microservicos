package br.com.fiap.order.domain.entity;

import br.com.fiap.order.domain.enums.OrderStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLRestriction;

@SuperBuilder
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(schema = "order_management", name = "orders")
@SQLRestriction("deleted = false")
public class Order extends BaseEntity {

  @JsonIgnore
  @Builder.Default
  private Boolean active = true;
  private UUID companyId;
  private UUID customerId;
  @JsonIgnore
  @Enumerated(EnumType.STRING)
  @Column(name = "status")
  private OrderStatus orderStatus;
  @JsonIgnore
  @CreationTimestamp
  private LocalDateTime orderDate;
  @JsonIgnore
  private BigDecimal orderTotalAmount;
  @OneToMany(targetEntity = OrderItem.class, mappedBy = "order", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  private List<OrderItem> orderItems;

  public void calculateTotalAmountOrderItem() {
    for (OrderItem orderItem : getOrderItems()) {
      orderItem.setOrder(this);
      orderItem.calculateTotalItemAmount();
    }
  }

  public void calculateTotalOrderAmout() {
    orderTotalAmount = orderItems.stream()
        .map(orderItem -> orderItem.getQuantity().multiply(orderItem.getPrice()))
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  @Override
  public String toString() {
    return "Order{" +
        "id=" + id +
        '}';
  }
}
