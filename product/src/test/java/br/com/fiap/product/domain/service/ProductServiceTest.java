package br.com.fiap.product.domain.service;

import static br.com.fiap.product.domain.fields.ProductFields.PRODUCT_ID_FIELD;
import static br.com.fiap.product.domain.fields.ProductFields.PRODUCT_SKU_FIELD;
import static br.com.fiap.product.domain.messages.ProductMessages.PRODUCT_NOT_FOUND_WITH_ID_MESSAGE;
import static br.com.fiap.product.domain.messages.ProductMessages.PRODUCT_NOT_FOUND_WITH_SKU_MESSAGE;
import static br.com.fiap.product.shared.testdata.ProductTestData.DEFAULT_PRODUCT_ID;
import static br.com.fiap.product.shared.testdata.ProductTestData.DEFAULT_PRODUCT_SKU;
import static br.com.fiap.product.shared.testdata.ProductTestData.createNewProduct;
import static br.com.fiap.product.shared.testdata.ProductTestData.createProduct;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import br.com.fiap.product.domain.entity.Product;
import br.com.fiap.product.domain.exception.NoResultException;
import br.com.fiap.product.infrastructure.repository.ProductRepository;
import br.com.fiap.product.shared.testdata.ProductTestData;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.FieldError;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

  public static final int PAGE_NUMBER = 0;
  public static final int PAGE_SIZE = 1;
  @Mock
  private ProductRepository productRepository;
  @InjectMocks
  private ProductService productService;

  @Test
  void shouldSaveProduct() {
    var product = createNewProduct();
    var productWithId = createProduct();
    when(productRepository.save(product)).thenReturn(productWithId);

    var productSaved = productService.save(product);

    assertThat(productSaved).isNotNull();
    assertThat(productSaved.getId()).isNotNull().isEqualTo(productWithId.getId());
    assertThat(productSaved.getSku()).isNotNull().isEqualTo(productWithId.getSku());
    assertThat(productSaved.getDescription()).isNotNull().isEqualTo(productWithId.getDescription());
  }

  @Test
  void shouldFindProductByIdRequired() {
    var product = createProduct();
    when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

    var productFound = productService.findByIdRequired(product.getId());

    assertThat(productFound).isNotNull();
    assertThat(productFound.getId()).isNotNull().isEqualTo(product.getId());
    assertThat(productFound.getSku()).isNotNull().isEqualTo(product.getSku());
    assertThat(productFound.getDescription()).isNotBlank().isEqualTo(product.getDescription());
  }

  @Test
  void shouldThrowNoResultExceptionWhenFindProductByIdRequiredAndItDoesNotExist() {
    var productNotFoundId = DEFAULT_PRODUCT_ID;
    when(productRepository.findById(productNotFoundId)).thenThrow(
        new NoResultException(new FieldError(this.getClass().getSimpleName(),
            PRODUCT_ID_FIELD, PRODUCT_NOT_FOUND_WITH_ID_MESSAGE.formatted(productNotFoundId))));

    assertThatThrownBy(() -> productService.findByIdRequired(productNotFoundId))
        .isInstanceOf(NoResultException.class)
        .hasMessage(PRODUCT_NOT_FOUND_WITH_ID_MESSAGE.formatted(productNotFoundId));
  }

  @Test
  void shouldFindProductBySku() {
    var product = createProduct();
    when(productRepository.findBySku(product.getSku())).thenReturn(Optional.of(product));

    var productFoundOptional = productService.findBySku(product.getSku());
    var productFound = productFoundOptional.orElse(null);

    assertThat(productFound).isNotNull();
    assertThat(productFound.getId()).isNotNull().isEqualTo(product.getId());
    assertThat(productFound.getSku()).isNotNull().isEqualTo(product.getSku());
    assertThat(productFound.getDescription()).isNotBlank().isEqualTo(product.getDescription());
  }

  @Test
  void shouldThrowNoResultExceptionWhenFindProductBySkuAndItDoesNotExist() {
    when(productRepository.findBySku(DEFAULT_PRODUCT_SKU)).thenReturn(Optional.empty());

    var product = productService.findBySku(DEFAULT_PRODUCT_SKU);

    assertThat(product.isPresent()).isEqualTo(Boolean.FALSE);
  }

  @Test
  void shouldFindProductBySkuRequired() {
    var product = createProduct();
    when(productRepository.findBySku(product.getSku())).thenReturn(Optional.of(product));

    var productFound = productService.findBySkuRequired(product.getSku());

    assertThat(productFound).isNotNull();
    assertThat(productFound.getId()).isNotNull().isEqualTo(product.getId());
    assertThat(productFound.getSku()).isNotNull().isEqualTo(product.getSku());
    assertThat(productFound.getDescription()).isNotBlank().isEqualTo(product.getDescription());
  }

  @Test
  void shouldThrowNoResultExceptionWhenFindProductBySkuRequiredAndItDoesNotExist() {
    var productSkuNotFound = DEFAULT_PRODUCT_SKU;
    when(productRepository.findBySku(productSkuNotFound)).thenThrow(
        new NoResultException(new FieldError(this.getClass().getSimpleName(),
            PRODUCT_SKU_FIELD, PRODUCT_NOT_FOUND_WITH_SKU_MESSAGE.formatted(productSkuNotFound))));

    assertThatThrownBy(() -> productService.findBySkuRequired(productSkuNotFound))
        .isInstanceOf(NoResultException.class)
        .hasMessage(PRODUCT_NOT_FOUND_WITH_SKU_MESSAGE.formatted(productSkuNotFound));
  }

  @Test
  void shouldFindProductBySkuAndDescriptionPaginatedWhenProductExists() {
    var product = ProductTestData.createProduct();
    var sku = product.getSku();
    var description = product.getDescription();
    var products = List.of(product);
    var pageable = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);
    var size = products.size();
    var page = new PageImpl<>(products, pageable, size);
    when(productRepository.queryProductsBySkuLikeIgnoreCaseOrDescription(sku, description,
        pageable)).thenReturn(page);

    var productsFound = productService.queryProductsBySkuLikeIgnoreCaseOrDescription(sku,
        description, pageable);

    assertThat(productsFound).isNotNull();
    assertThat(productsFound.getSize()).isEqualTo(PAGE_SIZE);
    assertThat(productsFound.getTotalPages()).isEqualTo(size);
    assertThat(productsFound.getTotalElements()).isEqualTo(size);
  }

  @Test
  void shouldFindProductBySkuPaginatedWhenProductExists() {
    var product = ProductTestData.createProduct();
    var sku = product.getSku();
    var description = "";
    var products = List.of(product);
    var pageable = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);
    var size = products.size();
    var page = new PageImpl<>(products, pageable, size);
    when(productRepository.queryProductsBySkuLikeIgnoreCaseOrDescription(sku, description,
        pageable)).thenReturn(page);

    var productsFound = productService.queryProductsBySkuLikeIgnoreCaseOrDescription(sku,
        description, pageable);

    assertThat(productsFound).isNotNull();
    assertThat(productsFound.getSize()).isEqualTo(PAGE_SIZE);
    assertThat(productsFound.getTotalPages()).isEqualTo(size);
    assertThat(productsFound.getTotalElements()).isEqualTo(size);
  }

  @Test
  void shouldFindProductByDescriptionPaginatedWhenProductExists() {
    var product = ProductTestData.createProduct();
    var sku = "";
    var description = product.getDescription();
    var products = List.of(product);
    var pageable = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);
    var size = products.size();
    var page = new PageImpl<>(products, pageable, size);
    when(productRepository.queryProductsBySkuLikeIgnoreCaseOrDescription(sku, description,
        pageable)).thenReturn(page);

    var productsFound = productService.queryProductsBySkuLikeIgnoreCaseOrDescription(sku,
        description, pageable);

    assertThat(productsFound).isNotNull();
    assertThat(productsFound.getSize()).isEqualTo(PAGE_SIZE);
    assertThat(productsFound.getTotalPages()).isEqualTo(size);
    assertThat(productsFound.getTotalElements()).isEqualTo(size);
  }

  @Test
  void shouldFindNothingWhenProductWasNotFoundBySkuAndDescription() {
    var product = ProductTestData.createProduct();
    var sku = product.getSku();
    var description = product.getDescription();
    var products = new ArrayList<Product>();
    var pageable = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);
    var size = 0;
    var page = new PageImpl<>(products, pageable, size);
    when(productRepository.queryProductsBySkuLikeIgnoreCaseOrDescription(sku, description,
        pageable)).thenReturn(page);

    var productsFound = productService.queryProductsBySkuLikeIgnoreCaseOrDescription(sku,
        description, pageable);

    assertThat(productsFound).isNotNull();
    assertThat(productsFound.getSize()).isEqualTo(PAGE_SIZE);
    assertThat(productsFound.getTotalPages()).isEqualTo(size);
    assertThat(productsFound.getTotalElements()).isEqualTo(size);
  }

  @Test
  void shouldFindNothingWhenProductWasNotFoundBySku() {
    var product = ProductTestData.createProduct();
    var sku = product.getSku();
    var description = "";
    var products = new ArrayList<Product>();
    var pageable = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);
    var size = 0;
    var page = new PageImpl<>(products, pageable, size);
    when(productRepository.queryProductsBySkuLikeIgnoreCaseOrDescription(sku, description,
        pageable)).thenReturn(page);

    var productsFound = productService.queryProductsBySkuLikeIgnoreCaseOrDescription(sku,
        description, pageable);

    assertThat(productsFound).isNotNull();
    assertThat(productsFound.getSize()).isEqualTo(PAGE_SIZE);
    assertThat(productsFound.getTotalPages()).isEqualTo(size);
    assertThat(productsFound.getTotalElements()).isEqualTo(size);
  }

  @Test
  void shouldFindNothingWhenProductWasNotFoundByDescription() {
    var product = ProductTestData.createProduct();
    var sku = "";
    var description = product.getDescription();
    var products = new ArrayList<Product>();
    var pageable = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);
    var size = 0;
    var page = new PageImpl<>(products, pageable, size);
    when(productRepository.queryProductsBySkuLikeIgnoreCaseOrDescription(sku, description,
        pageable)).thenReturn(page);

    var productsFound = productService.queryProductsBySkuLikeIgnoreCaseOrDescription(sku,
        description, pageable);

    assertThat(productsFound).isNotNull();
    assertThat(productsFound.getSize()).isEqualTo(PAGE_SIZE);
    assertThat(productsFound.getTotalPages()).isEqualTo(size);
    assertThat(productsFound.getTotalElements()).isEqualTo(size);
  }
}
