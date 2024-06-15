package br.com.fiap.product.application.usecase;

import static br.com.fiap.product.domain.fields.ProductFields.PRODUCT_SKU_FIELD;
import static br.com.fiap.product.domain.messages.ProductMessages.PRODUCT_ALREADY_REGISTERED_WITH_SKU_MESSAGE;
import static br.com.fiap.product.shared.testdata.ProductTestData.createNewProduct;
import static br.com.fiap.product.shared.testdata.ProductTestData.createProduct;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.fiap.product.application.validator.ProductSkuAlreadyRegisteredValidator;
import br.com.fiap.product.domain.entity.Product;
import br.com.fiap.product.domain.exception.DuplicatedException;
import br.com.fiap.product.domain.service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.FieldError;

@ExtendWith(MockitoExtension.class)
class CreateProductUseCaseTest {

  @Mock
  private ProductService productService;
  @Mock
  private ProductSkuAlreadyRegisteredValidator productSkuAlreadyRegisteredValidator;
  @InjectMocks
  private CreateProductUseCase createProductUseCase;

  @Test
  void shouldCreateProduct() {
    var product = createNewProduct();
    var productWithId = createProduct();
    when(productService.save(any(Product.class))).thenReturn(productWithId);

    var productSaved = createProductUseCase.execute(product);

    assertThat(productSaved).isNotNull();
    assertThat(productSaved.getId()).isNotNull().isEqualTo(productWithId.getId());
    assertThat(productSaved.getSku()).isNotBlank().isEqualTo(productWithId.getSku());
    assertThat(productSaved.getDescription()).isNotBlank()
        .isEqualTo(productWithId.getDescription());
    verify(productSkuAlreadyRegisteredValidator).validate(product.getSku());
  }

  @Test
  void shouldThrowExceptionWhenProductSkuAlreadyExists() throws Exception {
    var product = createNewProduct();
    doThrow(new DuplicatedException(new FieldError(this.getClass().getSimpleName(),
        PRODUCT_SKU_FIELD, PRODUCT_ALREADY_REGISTERED_WITH_SKU_MESSAGE.formatted(product.getSku()))))
        .when(productSkuAlreadyRegisteredValidator).validate(product.getSku());

    assertThatThrownBy(() -> createProductUseCase.execute(product))
        .isInstanceOf(DuplicatedException.class)
        .hasMessage(PRODUCT_ALREADY_REGISTERED_WITH_SKU_MESSAGE.formatted(product.getSku()));
  }
}
