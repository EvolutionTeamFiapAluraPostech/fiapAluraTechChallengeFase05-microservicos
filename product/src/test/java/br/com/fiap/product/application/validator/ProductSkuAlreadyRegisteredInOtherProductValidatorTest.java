package br.com.fiap.product.application.validator;

import static br.com.fiap.product.shared.testdata.ProductTestData.createAlternativeProduct;
import static br.com.fiap.product.shared.testdata.ProductTestData.createProduct;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import br.com.fiap.product.domain.exception.DuplicatedException;
import br.com.fiap.product.domain.service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductSkuAlreadyRegisteredInOtherProductValidatorTest {

  @Mock
  private ProductService productService;
  @InjectMocks
  private ProductSkuAlreadyRegisteredInOtherProductValidator productSkuAlreadyRegisteredInOtherProductValidator;

  @Test
  void shouldValidateWhenProductSkuDoesNotExistInOtherProductValidator() {
    var product = createProduct();
    when(productService.findBySkuRequired(product.getSku())).thenReturn(product);
    assertThatCode(
        () -> productSkuAlreadyRegisteredInOtherProductValidator.validate(product.getId(),
            product.getSku())).doesNotThrowAnyException();
  }

  @Test
  void shouldThrowExceptionWhenProductSkuExistsInOtherProductValidator() {
    var product = createProduct();
    var alternativeProduct = createAlternativeProduct();
    when(productService.findBySkuRequired(product.getSku())).thenReturn(alternativeProduct);
    assertThatThrownBy(
        () -> productSkuAlreadyRegisteredInOtherProductValidator.validate(product.getId(),
            product.getSku())).isInstanceOf(DuplicatedException.class);
  }
}
