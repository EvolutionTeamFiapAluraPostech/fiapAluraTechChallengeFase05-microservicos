package br.com.fiap.order.infrastructure.httpclient.product.request;

import static br.com.fiap.order.infrastructure.httpclient.product.fields.ProductFields.PRODUCT_ID_FIELD;
import static br.com.fiap.order.infrastructure.httpclient.product.messages.ProductMessages.PRODUCT_NOT_FOUND_WITH_ID_MESSAGE;
import static br.com.fiap.order.infrastructure.httpclient.product.messages.ProductMessages.PRODUCT_WITH_INVALID_UUID_MESSAGE;

import br.com.fiap.order.domain.exception.NoResultException;
import br.com.fiap.order.domain.exception.ValidatorException;
import br.com.fiap.order.infrastructure.httpclient.product.ProductClient;
import br.com.fiap.order.infrastructure.httpclient.product.dto.ProductDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.FieldError;

@Service
public class GetProductByIdHttpRequest {

  private final ProductClient productClient;

  public GetProductByIdHttpRequest(ProductClient productClient) {
    this.productClient = productClient;
  }

  public ProductDto execute(String productId) {
    var responseEntityProductDto = productClient.getProductById(productId);
    validateResponseEntity(productId, responseEntityProductDto);
    return responseEntityProductDto.getBody();
  }

  private void validateResponseEntity(String productId,
      ResponseEntity<ProductDto> responseEntityProductDto) {
    if (responseEntityProductDto.getStatusCode().is4xxClientError()) {
      int value = responseEntityProductDto.getStatusCode().value();
      if (value == 400) {
        throw new ValidatorException(
            new FieldError(this.getClass().getSimpleName(), PRODUCT_ID_FIELD,
                PRODUCT_WITH_INVALID_UUID_MESSAGE.formatted(productId)));
      } else if (value == 404) {
        throw new NoResultException(
            new FieldError(this.getClass().getSimpleName(), PRODUCT_ID_FIELD,
                PRODUCT_NOT_FOUND_WITH_ID_MESSAGE.formatted(productId)));
      }
    }
  }
}
