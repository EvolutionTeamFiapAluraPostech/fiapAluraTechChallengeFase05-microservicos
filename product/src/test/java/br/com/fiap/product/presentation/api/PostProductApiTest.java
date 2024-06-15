package br.com.fiap.product.presentation.api;

import static br.com.fiap.product.shared.testdata.ProductTestData.createNewProduct;
import static br.com.fiap.product.shared.util.IsUUID.isUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.com.fiap.product.domain.entity.Product;
import br.com.fiap.product.shared.annotation.DatabaseTest;
import br.com.fiap.product.shared.annotation.IntegrationTest;
import br.com.fiap.product.shared.api.JsonUtil;
import br.com.fiap.product.shared.util.StringUtil;
import com.jayway.jsonpath.JsonPath;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

@IntegrationTest
@DatabaseTest
class PostProductApiTest {

  private static final String URL_PRODUCTS = "/products";
  private final MockMvc mockMvc;
  private final EntityManager entityManager;

  @Autowired
  PostProductApiTest(MockMvc mockMvc, EntityManager entityManager) {
    this.mockMvc = mockMvc;
    this.entityManager = entityManager;
  }

  private Product getCreatedProduct(String id) {
    return entityManager.find(Product.class, UUID.fromString(id));
  }

  @Test
  void shouldReturnCreatedWhenSaveProduct() throws Exception {
    var product = createNewProduct();
    var productInputDto = JsonUtil.toJson(product);

    var request = post(URL_PRODUCTS)
        .contentType(APPLICATION_JSON)
        .content(productInputDto);
    var mockMvcResult = mockMvc.perform(request)
        .andExpect(status().isCreated())
        .andExpect(content().contentType(APPLICATION_JSON))
        .andExpect(jsonPath("$.id", isUUID()))
        .andReturn();

    var contentAsString = mockMvcResult.getResponse().getContentAsString();
    var id = JsonPath.parse(contentAsString).read("$.id").toString();
    var productFound = getCreatedProduct(id);
    assertThat(productFound).isNotNull();
    assertThat(productFound.getSku()).isNotNull().isEqualTo(product.getSku());
    assertThat(productFound.getDescription()).isNotNull().isEqualTo(product.getDescription());
  }

  @ParameterizedTest
  @NullAndEmptySource
  void shouldReturnBadRequestWhenProductSkuWasNotFilled(String sku) throws Exception {
    var product = createNewProduct();
    product.setSku(sku);
    var productInputDto = JsonUtil.toJson(product);

    var request = post(URL_PRODUCTS)
        .contentType(APPLICATION_JSON)
        .content(productInputDto);
    mockMvc.perform(request)
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(APPLICATION_JSON));
  }

  @Test
  void shouldReturnBadRequestWhenProductSkuLengthIsBiggerThan20Characters() throws Exception {
    var product = createNewProduct();
    var sku = StringUtil.generateStringLength(21);
    product.setSku(sku);
    var productInputDto = JsonUtil.toJson(product);

    var request = post(URL_PRODUCTS)
        .contentType(APPLICATION_JSON)
        .content(productInputDto);
    mockMvc.perform(request)
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(APPLICATION_JSON));
  }

  @Test
  void shouldReturnConflictWhenProductSkuAlreadyExists() throws Exception {
    var product = createNewProduct();
    var productInputDto = JsonUtil.toJson(product);
    entityManager.persist(product);

    var request = post(URL_PRODUCTS)
        .contentType(APPLICATION_JSON)
        .content(productInputDto);
    mockMvc.perform(request)
        .andExpect(status().isConflict())
        .andExpect(content().contentType(APPLICATION_JSON));
  }

  @ParameterizedTest
  @NullAndEmptySource
  void shouldReturnBadRequestWhenProductDescriptionWasNotFilled(String description)
      throws Exception {
    var product = createNewProduct();
    product.setDescription(description);
    var productInputDto = JsonUtil.toJson(product);

    var request = post(URL_PRODUCTS)
        .contentType(APPLICATION_JSON)
        .content(productInputDto);
    mockMvc.perform(request)
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(APPLICATION_JSON));
  }

  @Test
  void shouldReturnBadRequestWhenProductDescriptionLengthIsInvalid() throws Exception {
    var product = createNewProduct();
    var description = StringUtil.generateStringLength(2);
    product.setDescription(description);
    var productInputDto = JsonUtil.toJson(product);

    var request = post(URL_PRODUCTS)
        .contentType(APPLICATION_JSON)
        .content(productInputDto);
    mockMvc.perform(request)
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(APPLICATION_JSON));
  }

  @Test
  void shouldReturnBadRequestWhenProductDescriptionLengthIsBiggerThan500Characters()
      throws Exception {
    var product = createNewProduct();
    var description = StringUtil.generateStringLength(501);
    product.setDescription(description);
    var productInputDto = JsonUtil.toJson(product);

    var request = post(URL_PRODUCTS)
        .contentType(APPLICATION_JSON)
        .content(productInputDto);
    mockMvc.perform(request)
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(APPLICATION_JSON));
  }

  @ParameterizedTest
  @NullAndEmptySource
  void shouldReturnBadRequestWhenProductUnitMeasurementWasNotFilled(String unitMeasurement)
      throws Exception {
    var product = createNewProduct();
    product.setUnitMeasurement(unitMeasurement);
    var productInputDto = JsonUtil.toJson(product);

    var request = post(URL_PRODUCTS)
        .contentType(APPLICATION_JSON)
        .content(productInputDto);
    mockMvc.perform(request)
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(APPLICATION_JSON));
  }

  @Test
  void shouldReturnBadRequestWhenProductUnitMeasurementLengthIsBiggerThan20Characters()
      throws Exception {
    var product = createNewProduct();
    var unitMeasurement = StringUtil.generateStringLength(21);
    product.setUnitMeasurement(unitMeasurement);
    var productInputDto = JsonUtil.toJson(product);

    var request = post(URL_PRODUCTS)
        .contentType(APPLICATION_JSON)
        .content(productInputDto);
    mockMvc.perform(request)
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(APPLICATION_JSON));
  }

  @Test
  void shouldReturnBadRequestWhenProductQuantityStockIsLowerThanZero() throws Exception {
    var product = createNewProduct();
    var quantityStock = new BigDecimal("-1.0");
    product.setQuantityStock(quantityStock);
    var productInputDto = JsonUtil.toJson(product);

    var request = post(URL_PRODUCTS)
        .contentType(APPLICATION_JSON)
        .content(productInputDto);
    mockMvc.perform(request)
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(APPLICATION_JSON));
  }

  @Test
  void shouldReturnBadRequestWhenProductPriceIsLowerThanZero() throws Exception {
    var product = createNewProduct();
    var price = new BigDecimal("-1.0");
    product.setPrice(price);
    var productInputDto = JsonUtil.toJson(product);

    var request = post(URL_PRODUCTS)
        .contentType(APPLICATION_JSON)
        .content(productInputDto);
    mockMvc.perform(request)
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(APPLICATION_JSON));
  }
}
