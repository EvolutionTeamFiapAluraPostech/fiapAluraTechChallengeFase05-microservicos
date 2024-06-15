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
class GetProductBySkuOrDescriptionUseCaseTest {

  public static final int PAGE_NUMBER = 0;
  public static final int PAGE_SIZE = 1;
  @Mock
  private ProductService productService;
  @InjectMocks
  private GetProductBySkuOrDescriptionUseCase getProductBySkuOrDescriptionUseCase;

  @Test
  void shouldGetProductBySkuPaginated() {
    var product = createProduct();
    var sku = product.getSku();
    var description = "";
    var products = List.of(product);
    var pageable = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);
    var size = products.size();
    var page = new PageImpl<>(products, pageable, size);
    when(productService.queryProductsBySkuLikeIgnoreCaseOrDescription(sku, description,
        pageable)).thenReturn(page);

    var productsFound = getProductBySkuOrDescriptionUseCase.execute(sku, description, pageable);

    assertThat(productsFound).isNotNull();
    assertThat(productsFound.getSize()).isEqualTo(PAGE_SIZE);
    assertThat(productsFound.getTotalPages()).isEqualTo(size);
    assertThat(productsFound.getTotalElements()).isEqualTo(size);
  }

  @Test
  void shouldGetProductByDescriptionPaginated() {
    var product = createProduct();
    var sku = "";
    var description = product.getDescription();
    var products = List.of(product);
    var pageable = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);
    var size = products.size();
    var page = new PageImpl<>(products, pageable, size);
    when(productService.queryProductsBySkuLikeIgnoreCaseOrDescription(sku, description,
        pageable)).thenReturn(page);

    var productsFound = getProductBySkuOrDescriptionUseCase.execute(sku, description, pageable);

    assertThat(productsFound).isNotNull();
    assertThat(productsFound.getSize()).isEqualTo(PAGE_SIZE);
    assertThat(productsFound.getTotalPages()).isEqualTo(size);
    assertThat(productsFound.getTotalElements()).isEqualTo(size);
  }

  @Test
  void shouldGetProductBySkuAndDescriptionPaginated() {
    var product = createProduct();
    var sku = product.getSku();
    var description = product.getDescription();
    var products = List.of(product);
    var pageable = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);
    var size = products.size();
    var page = new PageImpl<>(products, pageable, size);
    when(productService.queryProductsBySkuLikeIgnoreCaseOrDescription(sku, description,
        pageable)).thenReturn(page);

    var productsFound = getProductBySkuOrDescriptionUseCase.execute(sku, description, pageable);

    assertThat(productsFound).isNotNull();
    assertThat(productsFound.getSize()).isEqualTo(PAGE_SIZE);
    assertThat(productsFound.getTotalPages()).isEqualTo(size);
    assertThat(productsFound.getTotalElements()).isEqualTo(size);
  }

  @Test
  void shouldGetNothingWhenProductWasNotFoundBySku() {
    var product = createProduct();
    var sku = product.getSku();
    var description = "";
    var products = new ArrayList<Product>();
    var pageable = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);
    var size = 0;
    var page = new PageImpl<>(products, pageable, size);
    when(productService.queryProductsBySkuLikeIgnoreCaseOrDescription(sku, description,
        pageable)).thenReturn(page);

    var productsFound = getProductBySkuOrDescriptionUseCase.execute(sku, description, pageable);

    assertThat(productsFound).isNotNull();
    assertThat(productsFound.getSize()).isEqualTo(PAGE_SIZE);
    assertThat(productsFound.getTotalPages()).isEqualTo(size);
    assertThat(productsFound.getTotalElements()).isEqualTo(size);
  }

  @Test
  void shouldGetNothingWhenProductWasNotFoundByDescription() {
    var product = createProduct();
    var sku = "";
    var description = product.getDescription();
    var products = new ArrayList<Product>();
    var pageable = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);
    var size = 0;
    var page = new PageImpl<>(products, pageable, size);
    when(productService.queryProductsBySkuLikeIgnoreCaseOrDescription(sku, description,
        pageable)).thenReturn(page);

    var productsFound = getProductBySkuOrDescriptionUseCase.execute(sku, description, pageable);

    assertThat(productsFound).isNotNull();
    assertThat(productsFound.getSize()).isEqualTo(PAGE_SIZE);
    assertThat(productsFound.getTotalPages()).isEqualTo(size);
    assertThat(productsFound.getTotalElements()).isEqualTo(size);
  }

  @Test
  void shouldGetNothingWhenProductWasNotFoundBySkuAndDescription() {
    var product = createProduct();
    var sku = product.getSku();
    var description = product.getDescription();
    var products = new ArrayList<Product>();
    var pageable = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);
    var size = 0;
    var page = new PageImpl<>(products, pageable, size);
    when(productService.queryProductsBySkuLikeIgnoreCaseOrDescription(sku, description,
        pageable)).thenReturn(page);

    var productsFound = getProductBySkuOrDescriptionUseCase.execute(sku, description, pageable);

    assertThat(productsFound).isNotNull();
    assertThat(productsFound.getSize()).isEqualTo(PAGE_SIZE);
    assertThat(productsFound.getTotalPages()).isEqualTo(size);
    assertThat(productsFound.getTotalElements()).isEqualTo(size);
  }
}
