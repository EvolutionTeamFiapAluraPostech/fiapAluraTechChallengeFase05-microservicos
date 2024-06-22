package br.com.fiap.product.presentation.api;

import static br.com.fiap.product.shared.testdata.ProductTestData.createNewProduct;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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
class GetAllProductsBySkuOrDescriptionApiTest {

  private static final String URL_PRODUCTS = "/products/sku-description";
  private final MockMvc mockMvc;
  private final EntityManager entityManager;

  @Autowired
  GetAllProductsBySkuOrDescriptionApiTest(MockMvc mockMvc, EntityManager entityManager) {
    this.mockMvc = mockMvc;
    this.entityManager = entityManager;
  }

  private Product createAndPersistProduct() {
    var product = createNewProduct();
    return entityManager.merge(product);
  }

  @Test
  void shouldReturnOkWhenFindProductBySku() throws Exception {
    var product = createAndPersistProduct();
    var productPage = PageUtil.generatePageOfProduct(List.of(product));
    var productOutputDtoExpected = ProductOutputDto.toPage(productPage);

    var request = get(URL_PRODUCTS)
        .param("sku", product.getSku())
        .param("description", "");
    var result = mockMvc.perform(request)
        .andExpect(status().isOk())
        .andExpect(content().contentType(APPLICATION_JSON))
        .andReturn();

    var contentAsString = result.getResponse().getContentAsString();
    var productFound = JsonUtil.fromJson(contentAsString, ProductContent.class);
    assertThat(productFound.getContent().get(0).id()).isEqualTo(
        productOutputDtoExpected.getContent().get(0).id());
  }

  @Test
  void shouldReturnOkWhenFindProductByDescription() throws Exception {
    var product = createAndPersistProduct();
    var productPage = PageUtil.generatePageOfProduct(List.of(product));
    var productOutputDtoExpected = ProductOutputDto.toPage(productPage);

    var request = get(URL_PRODUCTS)
        .param("sku", "")
        .param("description", product.getDescription());
    var result = mockMvc.perform(request)
        .andExpect(status().isOk())
        .andExpect(content().contentType(APPLICATION_JSON))
        .andReturn();

    var contentAsString = result.getResponse().getContentAsString();
    var productFound = JsonUtil.fromJson(contentAsString, ProductContent.class);
    assertThat(productFound.getContent().get(0).id()).isEqualTo(
        productOutputDtoExpected.getContent().get(0).id());
  }

  @Test
  void shouldReturnOkWhenFindProductBySkuAndDescription() throws Exception {
    var product = createAndPersistProduct();
    var productPage = PageUtil.generatePageOfProduct(List.of(product));
    var productOutputDtoExpected = ProductOutputDto.toPage(productPage);

    var request = get(URL_PRODUCTS)
        .param("sku", product.getSku())
        .param("description", product.getDescription());
    var result = mockMvc.perform(request)
        .andExpect(status().isOk())
        .andExpect(content().contentType(APPLICATION_JSON))
        .andReturn();

    var contentAsString = result.getResponse().getContentAsString();
    var productFound = JsonUtil.fromJson(contentAsString, ProductContent.class);
    assertThat(productFound.getContent().get(0).id()).isEqualTo(
        productOutputDtoExpected.getContent().get(0).id());
  }

  @Test
  void shouldReturnOkWhenFindProductWasNotFound() throws Exception {
    var request = get(URL_PRODUCTS)
        .param("sku", "1ab")
        .param("description", "Teste");
    var result = mockMvc.perform(request)
        .andExpect(status().isOk())
        .andExpect(content().contentType(APPLICATION_JSON))
        .andReturn();

    var contentAsString = result.getResponse().getContentAsString();
    var productFound = JsonUtil.fromJson(contentAsString, ProductContent.class);
    assertThat(productFound.getContent()).isEmpty();
  }
}
