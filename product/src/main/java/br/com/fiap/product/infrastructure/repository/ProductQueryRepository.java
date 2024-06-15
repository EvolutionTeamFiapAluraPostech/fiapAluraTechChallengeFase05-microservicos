package br.com.fiap.product.infrastructure.repository;

import br.com.fiap.product.domain.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

public interface ProductQueryRepository {

  @Query(value = """
          SELECT p
          FROM Product p
          WHERE (:sku IS NULL OR UPPER(TRIM(p.sku)) LIKE CONCAT('%', UPPER(TRIM(:sku)), '%'))
            AND (:description IS NULL OR UPPER(TRIM(p.description)) LIKE CONCAT('%', UPPER(TRIM(:description)), '%'))
      """)
  Page<Product> queryProductsBySkuLikeIgnoreCaseOrDescription(String sku, String description,
      Pageable pageable);
}
