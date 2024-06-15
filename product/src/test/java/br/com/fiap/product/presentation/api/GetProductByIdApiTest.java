package br.com.fiap.product.presentation.api;

import static br.com.fiap.product.shared.testdata.ProductTestData.createNewProduct;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.com.fiap.product.domain.entity.Product;
import br.com.fiap.product.shared.annotation.DatabaseTest;
import br.com.fiap.product.shared.annotation.IntegrationTest;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

@IntegrationTest
@DatabaseTest
class GetProductByIdApiTest {

  private static final String URL_PRODUCTS = "/products/{id}";
  private final MockMvc mockMvc;
  private final EntityManager entityManager;

  @Autowired
  GetProductByIdApiTest(MockMvc mockMvc, EntityManager entityManager) {
    this.mockMvc = mockMvc;
    this.entityManager = entityManager;
  }

  private Product createAndPersistNewProduct() {
    var product = createNewProduct();
    return entityManager.merge(product);
  }

  @Test
  void shouldReturnOkWhenGetProductById() throws Exception {
    var product = createAndPersistNewProduct();

    var request = get(URL_PRODUCTS, product.getId());

    mockMvc.perform(request)
        .andExpect(status().isOk())
        .andExpect(content().contentType(APPLICATION_JSON))
        .andExpect(jsonPath("$.id", equalTo(product.getId().toString())))
        .andExpect(jsonPath("$.sku", equalTo(product.getSku())));
  }

  @ParameterizedTest
  @ValueSource(strings = {"1aB"})
  void shouldReturnBadRequestWhenProductIdIsInvalid(String productId) throws Exception {
    var request = get(URL_PRODUCTS, productId);

    mockMvc.perform(request)
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(APPLICATION_JSON));
  }

  @Test
  void shouldReturnNotFoundWhenGetProductByIdAndProductDoesNotExist() throws Exception {
    var notFoundProductId = "8f836f61-17c4-4f4f-935e-8625aad84ee8";
    var request = get(URL_PRODUCTS, notFoundProductId);

    mockMvc.perform(request)
        .andExpect(status().isNotFound())
        .andExpect(content().contentType(APPLICATION_JSON));
  }
}
