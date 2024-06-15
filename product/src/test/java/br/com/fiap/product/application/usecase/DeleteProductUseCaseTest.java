package br.com.fiap.product.application.usecase;

import static br.com.fiap.product.domain.fields.ProductFields.PRODUCT_ID_FIELD;
import static br.com.fiap.product.domain.messages.ProductMessages.PRODUCT_NOT_FOUND_WITH_ID_MESSAGE;
import static br.com.fiap.product.domain.messages.ProductMessages.PRODUCT_WITH_INVALID_UUID_MESSAGE;
import static br.com.fiap.product.shared.testdata.ProductTestData.DEFAULT_PRODUCT_ID;
import static br.com.fiap.product.shared.testdata.ProductTestData.DEFAULT_PRODUCT_ID_STRING;
import static br.com.fiap.product.shared.testdata.ProductTestData.createProduct;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.fiap.product.application.validator.UuidValidator;
import br.com.fiap.product.domain.entity.Product;
import br.com.fiap.product.domain.exception.NoResultException;
import br.com.fiap.product.domain.exception.ValidatorException;
import br.com.fiap.product.domain.service.ProductService;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.FieldError;

@ExtendWith(MockitoExtension.class)
class DeleteProductUseCaseTest {

  @Mock
  private ProductService productService;
  @Mock
  private UuidValidator uuidValidator;
  @InjectMocks
  private DeleteProductUseCase deleteProductUseCase;

  @Test
  void shouldDeleteProductByIdWhenItExists() {
    var product = createProduct();
    when(productService.findByIdRequired(product.getId())).thenReturn(product);

    assertThatCode(() -> deleteProductUseCase.execute(product.getId().toString()))
        .doesNotThrowAnyException();
    verify(uuidValidator).validate(product.getId().toString());
    verify(productService).save(product);
  }

  @Test
  void shouldThrowExceptionWhenProductIdIsInvalid() {
    var invalidProductId = "1aB";
    doThrow(new ValidatorException(new FieldError(this.getClass().getSimpleName(),
        PRODUCT_ID_FIELD, PRODUCT_WITH_INVALID_UUID_MESSAGE.formatted(invalidProductId))))
        .when(uuidValidator).validate(invalidProductId);

    assertThatThrownBy(() -> deleteProductUseCase.execute(invalidProductId))
        .isInstanceOf(ValidatorException.class)
        .hasMessage(PRODUCT_WITH_INVALID_UUID_MESSAGE.formatted(invalidProductId));
    verify(productService, never()).findByIdRequired(any(UUID.class));
    verify(productService, never()).save(any(Product.class));
  }

  @Test
  void shouldThrowExceptionWhenProductWasNotFoundById() {
    when(productService.findByIdRequired(DEFAULT_PRODUCT_ID)).thenThrow(new NoResultException(
        new FieldError(this.getClass().getSimpleName(), PRODUCT_ID_FIELD,
            PRODUCT_NOT_FOUND_WITH_ID_MESSAGE.formatted(DEFAULT_PRODUCT_ID))));

    assertThatThrownBy(() -> deleteProductUseCase.execute(DEFAULT_PRODUCT_ID_STRING))
        .isInstanceOf(NoResultException.class)
        .hasMessage(PRODUCT_NOT_FOUND_WITH_ID_MESSAGE.formatted(DEFAULT_PRODUCT_ID_STRING));
    verify(uuidValidator).validate(DEFAULT_PRODUCT_ID_STRING);
    verify(productService, never()).save(any(Product.class));
  }
}
