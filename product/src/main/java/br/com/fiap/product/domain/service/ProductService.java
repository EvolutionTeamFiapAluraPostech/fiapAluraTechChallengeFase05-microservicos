package br.com.fiap.product.domain.service;

import static br.com.fiap.product.domain.fields.ProductFields.PRODUCT_ID_FIELD;
import static br.com.fiap.product.domain.fields.ProductFields.PRODUCT_SKU_FIELD;
import static br.com.fiap.product.domain.messages.ProductMessages.PRODUCT_NOT_FOUND_WITH_ID_MESSAGE;
import static br.com.fiap.product.domain.messages.ProductMessages.PRODUCT_NOT_FOUND_WITH_SKU_MESSAGE;

import br.com.fiap.product.domain.entity.Product;
import br.com.fiap.product.domain.exception.NoResultException;
import br.com.fiap.product.infrastructure.repository.ProductRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.FieldError;

@Service
public class ProductService {

  private final ProductRepository productRepository;

  public ProductService(ProductRepository productRepository) {
    this.productRepository = productRepository;
  }

  public Product save(Product product) {
    return productRepository.save(product);
  }

  public Product findByIdRequired(UUID id) {
    return productRepository.findById(id)
        .orElseThrow(() -> new NoResultException(new FieldError(this.getClass().getSimpleName(),
            PRODUCT_ID_FIELD, PRODUCT_NOT_FOUND_WITH_ID_MESSAGE.formatted(id.toString()))));
  }

  public Optional<Product> findBySku(String sku) {
    return productRepository.findBySku(sku);
  }

  public Product findBySkuRequired(String sku) {
    return productRepository.findBySku(sku)
        .orElseThrow(() -> new NoResultException(new FieldError(this.getClass().getSimpleName(),
            PRODUCT_SKU_FIELD, PRODUCT_NOT_FOUND_WITH_SKU_MESSAGE.formatted(sku))));
  }

  public Page<Product> queryProductsBySkuLikeIgnoreCaseOrDescription(String sku, String description,
      Pageable pageable) {
    return productRepository.queryProductsBySkuLikeIgnoreCaseOrDescription(sku, description,
        pageable);
  }
}
