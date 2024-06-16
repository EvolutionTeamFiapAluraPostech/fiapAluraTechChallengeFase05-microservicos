package br.com.fiap.product.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLRestriction;

@SuperBuilder
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(schema = "product_management", name = "products")
@SQLRestriction("deleted = false")
public class Product extends BaseEntity {

  @Builder.Default
  private Boolean active = true;
  private String sku;
  private String description;
  private BigDecimal quantityStock;
  private BigDecimal price;
  private String unitMeasurement;
  private String imageUrl;
}
