package br.com.fiap.order.application.validator;

import static br.com.fiap.order.infrastructure.httpclient.product.fields.ProductFields.PRODUCT_ID_FIELD;
import static br.com.fiap.order.infrastructure.httpclient.product.messages.ProductMessages.PRODUCT_NOT_FOUND_WITH_ID_MESSAGE;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.when;

import br.com.fiap.order.domain.exception.NoResultException;
import br.com.fiap.order.infrastructure.httpclient.product.ProductClient;
import br.com.fiap.order.infrastructure.httpclient.product.dto.ProductDto;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;

@ExtendWith(MockitoExtension.class)
class ProductExistsValidatorTest {

  @Mock
  private ProductClient productClient;
  @InjectMocks
  private ProductExistsValidator productExistsValidator;

  @Test
  void shouldValidateProductIdWhenProductExists() {
    var productId = UUID.randomUUID().toString();
    var productDtoResponseEntity = new ResponseEntity<ProductDto>(HttpStatus.OK);
    when(productClient.getProductById(productId)).thenReturn(productDtoResponseEntity);

    assertThatCode(
        () -> productExistsValidator.validate(List.of(productId))).doesNotThrowAnyException();
  }

  @Test
  void shouldThrowNoResultExceptionWhenProductWasNotFoundById() {
    var productId = UUID.randomUUID().toString();
    when(productClient.getProductById(productId)).thenThrow(
        new NoResultException(new FieldError(this.getClass().getSimpleName(),
            PRODUCT_ID_FIELD, PRODUCT_NOT_FOUND_WITH_ID_MESSAGE.formatted(productId))));

    assertThatCode(() -> productExistsValidator.validate(List.of(productId)))
        .isInstanceOf(NoResultException.class)
        .hasMessage(PRODUCT_NOT_FOUND_WITH_ID_MESSAGE.formatted(productId));
  }
}