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
  private String companyName;
  private String companyEmail;
  private String companyDocNumber;
  private String companyDocNumberType;
  private String companyStreet;
  private String companyNumber;
  private String companyNeighborhood;
  private String companyCity;
  private String companyState;
  private String companyCountry;
  private String companyPostalCode;
  private BigDecimal companyLatitude;
  private BigDecimal companyLongitude;
  private UUID customerId;
  private String customerName;
  private String customerEmail;
  private String customerDocNumber;
  private String customerDocNumberType;
  private String customerStreet;
  private String customerNumber;
  private String customerNeighborhood;
  private String customerCity;
  private String customerState;
  private String customerCountry;
  private String customerPostalCode;
  private BigDecimal customerLatitude;
  private BigDecimal customerLongitude;
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
