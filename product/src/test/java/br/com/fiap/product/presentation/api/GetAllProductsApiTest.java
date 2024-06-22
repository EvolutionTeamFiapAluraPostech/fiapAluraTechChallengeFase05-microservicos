package br.com.fiap.product.presentation.api;

import static br.com.fiap.product.shared.testdata.ProductTestData.createNewProduct;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.com.fiap.product.domain.entity.Product;
import br.com.fiap.product.presentation.api.dto.ProductContent;
import br.com.fiap.product.presentation.api.dto.ProductOutputDto;
import br.com.fiap.product.shared.annotation.DatabaseTest;
import br.com.fiap.product.shared.annotation.IntegrationTest;
import br.com.fiap.product.shared.api.JsonUtil;
import br.com.fiap.product.shared.api.PageUtil;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

@IntegrationTest
@DatabaseTest
class GetAllProductsApiTest {

  private static final String URL_PRODUCTS = "/products";
  private final MockMvc mockMvc;
  private final EntityManager entityManager;

  @Autowired
  GetAllProductsApiTest(MockMvc mockMvc, EntityManager entityManager) {
    this.mockMvc = mockMvc;
    this.entityManager = entityManager;
  }

  private void createAndPersistProduct() {
    var product = createNewProduct();
    entityManager.merge(product);
  }

  private List<Product> getAllProducts() {
    var query = entityManager.createQuery("select p from Product p", Product.class);
    return query.getResultList();
  }

  private void deleteAllProducts() {
    entityManager.createQuery("delete from Product").executeUpdate();
  }

  @Test
  void shouldReturnOkWhenAllProductsExists() throws Exception {
    createAndPersistProduct();
    var products = getAllProducts();
    var productPage = PageUtil.generatePageOfProduct(products);
    var productExpected = ProductOutputDto.toPage(productPage);

    var request = get(URL_PRODUCTS);
    var mvcResult = mockMvc.perform(request)
        .andExpect(status().isOk())
        .andExpect(content().contentType(APPLICATION_JSON))
        .andExpect(jsonPath("$.content", hasSize(2)))
        .andReturn();

    var contentAsString = mvcResult.getResponse().getContentAsString();
    var productsFromApi = JsonUtil.fromJson(contentAsString, ProductContent.class);
    assertThat(productsFromApi.getContent()).usingRecursiveComparison().isEqualTo(productExpected);
  }

  @Test
  void shouldReturnOkWhenProductsDoNotExist() throws Exception {
    deleteAllProducts();
    var request = get(URL_PRODUCTS);
    mockMvc.perform(request)
        .andExpect(status().isOk())
        .andExpect(content().contentType(APPLICATION_JSON))
        .andExpect(jsonPath("$.content", hasSize(0)));
  }
}
