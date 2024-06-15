package br.com.fiap.product.application.validator;

import static br.com.fiap.product.domain.fields.ProductFields.PRODUCT_SKU_FIELD;
import static br.com.fiap.product.domain.messages.ProductMessages.PRODUCT_ALREADY_REGISTERED_WITH_SKU_MESSAGE;

import br.com.fiap.product.domain.exception.DuplicatedException;
import br.com.fiap.product.domain.service.ProductService;
import java.util.UUID;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;

@Component
public class ProductSkuAlreadyRegisteredInOtherProductValidator {

  private final ProductService productService;

  public ProductSkuAlreadyRegisteredInOtherProductValidator(ProductService productService) {
    this.productService = productService;
  }

  public void validate(UUID id, String sku) {
    var product = productService.findBySkuRequired(sku);
    if (product != null && product.getId() != null && !product.getId().equals(id)) {
      throw new DuplicatedException(
          new FieldError(this.getClass().getSimpleName(), PRODUCT_SKU_FIELD,
              PRODUCT_ALREADY_REGISTERED_WITH_SKU_MESSAGE.formatted(sku)));
    }
  }
}
