package br.com.fiap.order.application.validator;

import static br.com.fiap.order.infrastructure.httpclient.product.fields.ProductFields.PRODUCT_ID_FIELD;
import static br.com.fiap.order.infrastructure.httpclient.product.messages.ProductMessages.PRODUCT_NOT_FOUND_WITH_ID_MESSAGE;

import br.com.fiap.order.domain.exception.NoResultException;
import br.com.fiap.order.infrastructure.httpclient.product.ProductClient;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;

@Component
public class ProductExistsValidator {

  private final ProductClient productClient;

  public ProductExistsValidator(ProductClient productClient) {
    this.productClient = productClient;
  }

  public void validate(List<String> productsId) {
    productsId.forEach(productId -> {
      var productDtoResponseEntity = productClient.getProductById(productId);
      if (productDtoResponseEntity.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
        throw new NoResultException(
            new FieldError(this.getClass().getSimpleName(), PRODUCT_ID_FIELD,
                PRODUCT_NOT_FOUND_WITH_ID_MESSAGE.formatted(productId)));
      }
    });
  }
}
