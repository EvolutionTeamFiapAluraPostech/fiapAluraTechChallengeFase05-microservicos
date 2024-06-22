package br.com.fiap.product.application.usecase;

import static br.com.fiap.product.shared.testdata.ProductTestData.createProduct;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import br.com.fiap.product.domain.entity.Product;
import br.com.fiap.product.domain.service.ProductService;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class GetAllProductsUseCaseTest {

  private static final int PAGE_NUMBER = 0;
  private static final int PAGE_SIZE = 1;
  @Mock
  private ProductService productService;
  @InjectMocks
  private GetAllProductsUseCase getAllProductsUseCase;

  @Test
  void shouldGetAllProductsWhenProductsExists() {
    var product = createProduct();
    var products = List.of(product);
    var pageable = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);
    var size = products.size();
    var page = new PageImpl<>(products, pageable, size);
    when(productService.findAll(pageable)).thenReturn(page);

    var productsPage = getAllProductsUseCase.execute(pageable);

    assertThat(productsPage).isNotNull();
    assertThat(productsPage.getSize()).isEqualTo(size);
    assertThat(productsPage.getTotalPages()).isEqualTo(size);
    assertThat(productsPage.getTotalElements()).isEqualTo(size);
  }

  @Test
  void shouldReturnEmptyPageWhenProductsDoNotExists() {
    var products = new ArrayList<Product>();
    var pageable = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);
    var size = 0;
    var page = new PageImpl<>(products, pageable, size);
    when(productService.findAll(pageable)).thenReturn(page);

    var productsPage = getAllProductsUseCase.execute(pageable);

    assertThat(productsPage).isNotNull();
    assertThat(productsPage.getSize()).isEqualTo(PAGE_SIZE);
    assertThat(productsPage.getTotalPages()).isEqualTo(size);
    assertThat(productsPage.getTotalElements()).isEqualTo(size);
  }
}
