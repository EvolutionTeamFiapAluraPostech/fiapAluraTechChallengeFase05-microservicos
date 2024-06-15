package br.com.fiap.product.application.validator;

import static br.com.fiap.product.domain.fields.ProductFields.PRODUCT_SKU_FIELD;
import static br.com.fiap.product.domain.messages.ProductMessages.PRODUCT_ALREADY_REGISTERED_WITH_SKU_MESSAGE;

import br.com.fiap.product.domain.exception.DuplicatedException;
import br.com.fiap.product.domain.fields.ProductFields;
import br.com.fiap.product.domain.messages.ProductMessages;
import br.com.fiap.product.domain.service.ProductService;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;

@Component
public class ProductSkuAlreadyRegisteredValidator {

  private final ProductService productService;

  public ProductSkuAlreadyRegisteredValidator(ProductService productService) {
    this.productService = productService;
  }

  public void validate(String sku) {
    var product = productService.findBySku(sku);
    if (product.isPresent()) {
      throw new DuplicatedException(
          new FieldError(this.getClass().getSimpleName(), PRODUCT_SKU_FIELD,
              PRODUCT_ALREADY_REGISTERED_WITH_SKU_MESSAGE.formatted(sku)));
    }
  }
}
