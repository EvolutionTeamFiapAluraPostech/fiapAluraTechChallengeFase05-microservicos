package br.com.fiap.product.application.usecase;

import br.com.fiap.product.application.validator.UuidValidator;
import br.com.fiap.product.domain.entity.Product;
import br.com.fiap.product.domain.service.ProductService;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class GetProductByIdUseCase {

  private final ProductService productService;
  private final UuidValidator uuidValidator;

  public GetProductByIdUseCase(ProductService productService, UuidValidator uuidValidator) {
    this.productService = productService;
    this.uuidValidator = uuidValidator;
  }

  public Product execute(String id) {
    uuidValidator.validate(id);
    return productService.findByIdRequired(UUID.fromString(id));
  }
}
