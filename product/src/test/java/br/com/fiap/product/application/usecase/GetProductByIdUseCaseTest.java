package br.com.fiap.product.application.usecase;

import static br.com.fiap.product.domain.fields.ProductFields.PRODUCT_ID_FIELD;
import static br.com.fiap.product.domain.messages.ProductMessages.PRODUCT_NOT_FOUND_WITH_ID_MESSAGE;
import static br.com.fiap.product.domain.messages.ProductMessages.PRODUCT_WITH_INVALID_UUID_MESSAGE;
import static br.com.fiap.product.shared.testdata.ProductTestData.createProduct;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.fiap.product.application.validator.UuidValidator;
import br.com.fiap.product.domain.exception.NoResultException;
import br.com.fiap.product.domain.exception.ValidatorException;
import br.com.fiap.product.domain.service.ProductService;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.FieldError;

@ExtendWith(MockitoExtension.class)
class GetProductByIdUseCaseTest {

  @Mock
  private ProductService productService;
  @Mock
  private UuidValidator uuidValidator;
  @InjectMocks
  private GetProductByIdUseCase getProductByIdUseCase;

  @Test
  void shouldGetProductById() {
    var product = createProduct();
    when(productService.findByIdRequired(product.getId())).thenReturn(product);

    var productFound = getProductByIdUseCase.execute(product.getId().toString());

    assertThat(productFound).isNotNull();
    assertThat(productFound.getId()).isNotNull().isEqualTo(product.getId());
    verify(uuidValidator).validate(product.getId().toString());
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {"1aB"})
  void shouldThrowExceptionWhenIdIsInvalidUuid(String invalidProductId) {
    doThrow(new ValidatorException(new FieldError(this.getClass().getSimpleName(),
        PRODUCT_ID_FIELD, PRODUCT_WITH_INVALID_UUID_MESSAGE.formatted(invalidProductId))))
        .when(uuidValidator).validate(invalidProductId);

    assertThatThrownBy(() -> getProductByIdUseCase.execute(invalidProductId))
        .isInstanceOf(ValidatorException.class)
        .hasMessage(PRODUCT_WITH_INVALID_UUID_MESSAGE.formatted(invalidProductId));
  }

  @Test
  void shouldThrowExceptionWhenProductDoesNotExistWithId() {
    var notFoundProductId = UUID.fromString("8f836f61-17c4-4f4f-935e-8625aad84ee8");
    doThrow(new NoResultException(new FieldError(this.getClass().getSimpleName(), PRODUCT_ID_FIELD,
        PRODUCT_NOT_FOUND_WITH_ID_MESSAGE.formatted(notFoundProductId)))).when(productService)
        .findByIdRequired(notFoundProductId);

    assertThatThrownBy(() -> getProductByIdUseCase.execute(notFoundProductId.toString()))
        .isInstanceOf(NoResultException.class)
        .hasMessage(PRODUCT_NOT_FOUND_WITH_ID_MESSAGE.formatted(notFoundProductId));
  }
}
