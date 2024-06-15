package br.com.fiap.product.application.usecase;

import static br.com.fiap.product.domain.fields.ProductFields.PRODUCT_ID_FIELD;
import static br.com.fiap.product.domain.fields.ProductFields.PRODUCT_SKU_FIELD;
import static br.com.fiap.product.domain.messages.ProductMessages.PRODUCT_ALREADY_REGISTERED_WITH_SKU_MESSAGE;
import static br.com.fiap.product.domain.messages.ProductMessages.PRODUCT_NOT_FOUND_WITH_ID_MESSAGE;
import static br.com.fiap.product.domain.messages.ProductMessages.PRODUCT_WITH_INVALID_UUID_MESSAGE;
import static br.com.fiap.product.shared.testdata.ProductTestData.ALTERNATIVE_PRODUCT_DESCRIPTION;
import static br.com.fiap.product.shared.testdata.ProductTestData.ALTERNATIVE_PRODUCT_PRICE;
import static br.com.fiap.product.shared.testdata.ProductTestData.ALTERNATIVE_PRODUCT_QUANTITY_STOCK;
import static br.com.fiap.product.shared.testdata.ProductTestData.ALTERNATIVE_PRODUCT_SKU;
import static br.com.fiap.product.shared.testdata.ProductTestData.ALTERNATIVE_PRODUCT_UNIT_MEASUREMENT;
import static br.com.fiap.product.shared.testdata.ProductTestData.createProduct;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.fiap.product.application.validator.ProductSkuAlreadyRegisteredInOtherProductValidator;
import br.com.fiap.product.application.validator.UuidValidator;
import br.com.fiap.product.domain.entity.Product;
import br.com.fiap.product.domain.exception.NoResultException;
import br.com.fiap.product.domain.exception.ValidatorException;
import br.com.fiap.product.domain.service.ProductService;
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
class UpdateProductUseCaseTest {

  @Mock
  private ProductService productService;
  @Mock
  private UuidValidator uuidValidator;
  @Mock
  private ProductSkuAlreadyRegisteredInOtherProductValidator productSkuAlreadyRegisteredInOtherProductValidator;
  @InjectMocks
  private UpdateProductUseCase updateProductUseCase;

  private void updateProductAttributes(Product product) {
    product.setSku(ALTERNATIVE_PRODUCT_SKU);
    product.setDescription(ALTERNATIVE_PRODUCT_DESCRIPTION);
    product.setUnitMeasurement(ALTERNATIVE_PRODUCT_UNIT_MEASUREMENT);
    product.setQuantityStock(ALTERNATIVE_PRODUCT_QUANTITY_STOCK);
    product.setPrice(ALTERNATIVE_PRODUCT_PRICE);
  }

  @Test
  void shouldUpdateProduct() {
    var product = createProduct();
    when(productService.findByIdRequired(product.getId())).thenReturn(product);
    updateProductAttributes(product);
    when(productService.save(any(Product.class))).thenReturn(product);

    var productUpdated = updateProductUseCase.execute(product.getId().toString(), product);

    assertThat(productUpdated).isNotNull();
    assertThat(productUpdated.getId()).isNotNull().isEqualTo(product.getId());
    assertThat(productUpdated.getSku()).isNotBlank().isEqualTo(product.getSku());
    assertThat(productUpdated.getDescription()).isNotBlank().isEqualTo(product.getDescription());
    assertThat(productUpdated.getUnitMeasurement()).isNotBlank()
        .isEqualTo(product.getUnitMeasurement());
    assertThat(productUpdated.getQuantityStock()).isNotNull().isEqualTo(product.getQuantityStock());
    assertThat(productUpdated.getPrice()).isNotNull().isEqualTo(product.getPrice());
    verify(uuidValidator).validate(product.getId().toString());
    verify(productSkuAlreadyRegisteredInOtherProductValidator).validate(product.getId(),
        product.getSku());
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {"1aB"})
  void shouldThrowExceptionWhenProductIdIsInvalid(String productId) {
    var product = createProduct();
    doThrow(new ValidatorException(new FieldError(this.getClass().getSimpleName(), PRODUCT_ID_FIELD,
        PRODUCT_WITH_INVALID_UUID_MESSAGE.formatted(productId)))).when(uuidValidator)
        .validate(productId);

    assertThatThrownBy(() -> updateProductUseCase.execute(productId, product))
        .isInstanceOf(ValidatorException.class)
        .hasMessage(PRODUCT_WITH_INVALID_UUID_MESSAGE.formatted(productId));
    verify(uuidValidator).validate(productId);
    verify(productService, never()).findByIdRequired(product.getId());
    verify(productSkuAlreadyRegisteredInOtherProductValidator, never()).validate(product.getId(),
        product.getSku());
    verify(productService, never()).save(product);
  }

  @Test
  void shouldThrowExceptionWhenProductIdWasNotFoundToUpdate() {
    var product = createProduct();
    when(productService.findByIdRequired(product.getId())).thenThrow(new NoResultException(
        new FieldError(this.getClass().getSimpleName(), PRODUCT_ID_FIELD,
            PRODUCT_NOT_FOUND_WITH_ID_MESSAGE.formatted(product.getId().toString()))));

    assertThatThrownBy(() -> updateProductUseCase.execute(product.getId().toString(), product))
        .isInstanceOf(NoResultException.class)
        .hasMessage(PRODUCT_NOT_FOUND_WITH_ID_MESSAGE.formatted(product.getId().toString()));
    verify(uuidValidator).validate(product.getId().toString());
    verify(productSkuAlreadyRegisteredInOtherProductValidator, never()).validate(product.getId(),
        product.getSku());
    verify(productService, never()).save(product);
  }

  @Test
  void shouldThrowExceptionWhenProductSkuAlreadyRegisteredInOtherProduct() {
    var product = createProduct();
    when(productService.findByIdRequired(product.getId())).thenReturn(product);
    doThrow(new ValidatorException(
        new FieldError(this.getClass().getSimpleName(), PRODUCT_SKU_FIELD,
            PRODUCT_ALREADY_REGISTERED_WITH_SKU_MESSAGE))).when(
            productSkuAlreadyRegisteredInOtherProductValidator)
        .validate(product.getId(), product.getSku());

    assertThatThrownBy(() -> updateProductUseCase.execute(product.getId().toString(), product))
        .isInstanceOf(ValidatorException.class)
        .hasMessage(PRODUCT_ALREADY_REGISTERED_WITH_SKU_MESSAGE);
    verify(uuidValidator).validate(product.getId().toString());
    verify(productService, never()).save(product);
  }
}
