package br.com.fiap.product.application.usecase;

import br.com.fiap.product.application.validator.ProductSkuAlreadyRegisteredInOtherProductValidator;
import br.com.fiap.product.application.validator.UuidValidator;
import br.com.fiap.product.domain.entity.Product;
import br.com.fiap.product.domain.service.ProductService;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateProductUseCase {

  private final ProductService productService;
  private final UuidValidator uuidValidator;
  private final ProductSkuAlreadyRegisteredInOtherProductValidator productSkuAlreadyRegisteredInOtherProductValidator;

  public UpdateProductUseCase(ProductService productService, UuidValidator uuidValidator,
      ProductSkuAlreadyRegisteredInOtherProductValidator productSkuAlreadyRegisteredInOtherProductValidator) {
    this.productService = productService;
    this.uuidValidator = uuidValidator;
    this.productSkuAlreadyRegisteredInOtherProductValidator = productSkuAlreadyRegisteredInOtherProductValidator;
  }

  @Transactional
  public Product execute(String id, Product product) {
    uuidValidator.validate(id);
    var productUuid = UUID.fromString(id);
    var productToSave = productService.findByIdRequired(productUuid);
    productSkuAlreadyRegisteredInOtherProductValidator.validate(productUuid, product.getSku());
    updateProductToSaveAttributesFromProduct(productToSave, product);
    return productService.save(productToSave);
  }

  private void updateProductToSaveAttributesFromProduct(Product productToSave, Product product) {
    productToSave.setSku(product.getSku());
    productToSave.setDescription(product.getDescription());
    productToSave.setUnitMeasurement(product.getUnitMeasurement());
    productToSave.setQuantityStock(product.getQuantityStock());
    productToSave.setPrice(product.getPrice());
  }
}
