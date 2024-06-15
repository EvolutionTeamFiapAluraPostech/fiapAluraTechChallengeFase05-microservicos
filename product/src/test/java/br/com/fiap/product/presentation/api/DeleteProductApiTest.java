package br.com.fiap.product.presentation.api;

import static br.com.fiap.product.shared.testdata.ProductTestData.createNewProduct;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.com.fiap.product.domain.entity.Product;
import br.com.fiap.product.shared.annotation.DatabaseTest;
import br.com.fiap.product.shared.annotation.IntegrationTest;
import br.com.fiap.product.shared.testdata.ProductTestData;
import jakarta.persistence.EntityManager;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

@IntegrationTest
@DatabaseTest
class DeleteProductApiTest {

  private static final String URL_PRODUCTS = "/products/{id}";
  private final MockMvc mockMvc;
  private final EntityManager entityManager;

  @Autowired
  DeleteProductApiTest(MockMvc mockMvc, EntityManager entityManager) {
    this.mockMvc = mockMvc;
    this.entityManager = entityManager;
  }

  private Product createAndPersistProduct() {
    var product = createNewProduct();
    return entityManager.merge(product);
  }

  private Product getProductById(UUID id) {
    return entityManager.find(Product.class, id);
  }

  @Test
  void shouldDeleteProductByIdWhenItExists() throws Exception {
    var product = createAndPersistProduct();

    var request = delete(URL_PRODUCTS, product.getId());

    mockMvc.perform(request)
        .andExpect(status().isNoContent());

    var productDeleted = getProductById(product.getId());
    assertThat(productDeleted).isNotNull();
    assertThat(productDeleted.getId()).isNotNull().isEqualTo(product.getId());
    assertThat(productDeleted.getDeleted()).isNotNull().isTrue();
  }

  @Test
  void shouldReturnBadRequestWhenProductIdIsInvalid() throws Exception {
    var id = "1aB";

    var request = delete(URL_PRODUCTS, id);

    mockMvc.perform(request)
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldReturnNotFoundWhenProductDoesNotExist() throws Exception {
    var id = ProductTestData.DEFAULT_PRODUCT_ID;

    var request = delete(URL_PRODUCTS, id);

    mockMvc.perform(request)
        .andExpect(status().isNotFound());
  }
}
