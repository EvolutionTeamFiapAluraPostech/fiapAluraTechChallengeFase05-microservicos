package br.com.fiap.product.application.usecase;

import br.com.fiap.product.application.validator.ProductSkuAlreadyRegisteredValidator;
import br.com.fiap.product.domain.entity.Product;
import br.com.fiap.product.domain.service.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateProductUseCase {

  private final ProductService productService;
  private final ProductSkuAlreadyRegisteredValidator productSkuAlreadyRegisteredValidator;

  public CreateProductUseCase(ProductService productService,
      ProductSkuAlreadyRegisteredValidator productSkuAlreadyRegisteredValidator) {
    this.productService = productService;
    this.productSkuAlreadyRegisteredValidator = productSkuAlreadyRegisteredValidator;
  }

  @Transactional
  public Product execute(Product product) {
    productSkuAlreadyRegisteredValidator.validate(product.getSku());
    return productService.save(product);
  }
}
