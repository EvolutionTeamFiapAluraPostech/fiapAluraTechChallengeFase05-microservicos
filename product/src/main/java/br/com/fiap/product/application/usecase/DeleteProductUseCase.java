package br.com.fiap.product.application.usecase;

import br.com.fiap.product.application.validator.UuidValidator;
import br.com.fiap.product.domain.service.ProductService;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeleteProductUseCase {

  private final ProductService productService;
  private final UuidValidator uuidValidator;

  public DeleteProductUseCase(ProductService productService, UuidValidator uuidValidator) {
    this.productService = productService;
    this.uuidValidator = uuidValidator;
  }

  @Transactional
  public void execute(String id) {
    uuidValidator.validate(id);
    var product = productService.findByIdRequired(UUID.fromString(id));
    product.setDeleted(true);
    productService.save(product);
  }
}
