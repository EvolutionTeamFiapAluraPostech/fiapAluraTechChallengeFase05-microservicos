package br.com.fiap.product.presentation.api;

import static br.com.fiap.product.shared.testdata.ProductTestData.ALTERNATIVE_PRODUCT_DESCRIPTION;
import static br.com.fiap.product.shared.testdata.ProductTestData.ALTERNATIVE_PRODUCT_PRICE;
import static br.com.fiap.product.shared.testdata.ProductTestData.ALTERNATIVE_PRODUCT_QUANTITY_STOCK;
import static br.com.fiap.product.shared.testdata.ProductTestData.ALTERNATIVE_PRODUCT_SKU;
import static br.com.fiap.product.shared.testdata.ProductTestData.ALTERNATIVE_PRODUCT_UNIT_MEASUREMENT;
import static br.com.fiap.product.shared.testdata.ProductTestData.createNewAlternativeProduct;
import static br.com.fiap.product.shared.testdata.ProductTestData.createNewProduct;
import static br.com.fiap.product.shared.testdata.ProductTestData.createProduct;
import static br.com.fiap.product.shared.util.IsUUID.isUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.com.fiap.product.domain.entity.Product;
import br.com.fiap.product.shared.annotation.DatabaseTest;
import br.com.fiap.product.shared.annotation.IntegrationTest;
import br.com.fiap.product.shared.api.JsonUtil;
import br.com.fiap.product.shared.testdata.ProductTestData;
import br.com.fiap.product.shared.util.StringUtil;
import com.jayway.jsonpath.JsonPath;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

@IntegrationTest
@DatabaseTest
class PutProductApiTest {

  private static final String URL_PRODUCTS = "/products/{id}";
  private final MockMvc mockMvc;
  private final EntityManager entityManager;

  @Autowired
  PutProductApiTest(MockMvc mockMvc, EntityManager entityManager) {
    this.mockMvc = mockMvc;
    this.entityManager = entityManager;
  }

  private Product createAndPersistProduct() {
    var product = createNewProduct();
    return entityManager.merge(product);
  }

  private Product createAndPersistNewProduct() {
    var product = createNewAlternativeProduct();
    return entityManager.merge(product);
  }

  private void updateProductAttributes(Product product) {
    product.setSku(ALTERNATIVE_PRODUCT_SKU);
    product.setDescription(ALTERNATIVE_PRODUCT_DESCRIPTION);
    product.setUnitMeasurement(ALTERNATIVE_PRODUCT_UNIT_MEASUREMENT);
    product.setQuantityStock(ALTERNATIVE_PRODUCT_QUANTITY_STOCK);
    product.setPrice(ALTERNATIVE_PRODUCT_PRICE);
  }

  private Product getProductById(UUID id) {
    return entityManager.find(Product.class, id);
  }

  @Test
  void shouldReturnAcceptedWhenUpdateProduct() throws Exception {
    var product = createAndPersistProduct();
    updateProductAttributes(product);
    var productInputDto = JsonUtil.toJson(product);

    var request = put(URL_PRODUCTS, product.getId())
        .contentType(APPLICATION_JSON)
        .content(productInputDto);
    var mockMvcResult = mockMvc.perform(request)
        .andExpect(status().isAccepted())
        .andExpect(jsonPath("$.id", isUUID()))
        .andReturn();

    var contentAsString = mockMvcResult.getResponse().getContentAsString();
    var id = JsonPath.parse(contentAsString).read("$.id").toString();
    var productFound = getProductById(UUID.fromString(id));
    assertThat(productFound).isNotNull();
    assertThat(productFound.getId()).isNotNull().isEqualTo(product.getId());
    assertThat(productFound.getSku()).isNotBlank().isEqualTo(product.getSku());
    assertThat(productFound.getDescription()).isNotBlank().isEqualTo(product.getDescription());
    assertThat(productFound.getUnitMeasurement()).isNotBlank()
        .isEqualTo(product.getUnitMeasurement());
    assertThat(productFound.getQuantityStock()).isNotNull().isEqualTo(product.getQuantityStock());
    assertThat(productFound.getPrice()).isNotNull().isEqualTo(product.getPrice());
  }

  @ParameterizedTest
  @NullAndEmptySource
  void shouldReturnBadRequestWhenProductSkuWasNotFilled(String sku) throws Exception {
    var product = createProduct();
    updateProductAttributes(product);
    product.setSku(sku);
    var productInputDto = JsonUtil.toJson(product);

    var request = put(URL_PRODUCTS, product.getId())
        .contentType(APPLICATION_JSON)
        .content(productInputDto);
    mockMvc.perform(request)
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldReturnBadRequestWhenProductSkuMaxLengthIsGreaterThan20Characters() throws Exception {
    var product = createProduct();
    updateProductAttributes(product);
    var sku = StringUtil.generateStringLength(21);
    product.setSku(sku);
    var productInputDto = JsonUtil.toJson(product);

    var request = put(URL_PRODUCTS, product.getId())
        .contentType(APPLICATION_JSON)
        .content(productInputDto);
    mockMvc.perform(request)
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldReturnConflictWhenProductSkuAlreadyExistsInOtherProduct() throws Exception {
    var productWithSku = createAndPersistProduct();
    var product = createAndPersistNewProduct();
    var productAux = ProductTestData.createProduct();
    productAux.setId(product.getId());
    productAux.setSku(productWithSku.getSku());
    var productInputDto = JsonUtil.toJson(productAux);

    var request = put(URL_PRODUCTS, productAux.getId())
        .contentType(APPLICATION_JSON)
        .content(productInputDto);
    mockMvc.perform(request)
        .andExpect(status().isConflict());
  }

  @ParameterizedTest
  @NullAndEmptySource
  void shouldReturnBadRequestWhenProductDescriptionWasNotFilled(String description)
      throws Exception {
    var product = createProduct();
    updateProductAttributes(product);
    product.setSku(description);
    var productInputDto = JsonUtil.toJson(product);

    var request = put(URL_PRODUCTS, product.getId())
        .contentType(APPLICATION_JSON)
        .content(productInputDto);
    mockMvc.perform(request)
        .andExpect(status().isBadRequest());
  }

  @ParameterizedTest
  @ValueSource(ints = {1, 501})
  void shouldReturnBadRequestWhenProductDescriptionLengthIsInvalid(Integer invalidLength)
      throws Exception {
    var product = createProduct();
    updateProductAttributes(product);
    var description = StringUtil.generateStringLength(invalidLength);
    product.setDescription(description);
    var productInputDto = JsonUtil.toJson(product);

    var request = put(URL_PRODUCTS, product.getId())
        .contentType(APPLICATION_JSON)
        .content(productInputDto);
    mockMvc.perform(request)
        .andExpect(status().isBadRequest());
  }

  @ParameterizedTest
  @NullAndEmptySource
  void shouldReturnBadRequestWhenProductUnitMeasurementWasNotFilled(String unitMeasurement)
      throws Exception {
    var product = createProduct();
    updateProductAttributes(product);
    product.setUnitMeasurement(unitMeasurement);
    var productInputDto = JsonUtil.toJson(product);

    var request = put(URL_PRODUCTS, product.getId())
        .contentType(APPLICATION_JSON)
        .content(productInputDto);
    mockMvc.perform(request)
        .andExpect(status().isBadRequest());
  }

  @ParameterizedTest
  @ValueSource(ints = {21})
  void shouldReturnBadRequestWhenProductUnitMeasurementLengthIsInvalid(Integer invalidLength)
      throws Exception {
    var product = createProduct();
    updateProductAttributes(product);
    var unitMeasurement = StringUtil.generateStringLength(invalidLength);
    product.setUnitMeasurement(unitMeasurement);
    var productInputDto = JsonUtil.toJson(product);

    var request = put(URL_PRODUCTS, product.getId())
        .contentType(APPLICATION_JSON)
        .content(productInputDto);
    mockMvc.perform(request)
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldReturnBadRequestWhenProductQuantityStockIsLowerThanZero() throws Exception {
    var product = createProduct();
    updateProductAttributes(product);
    product.setQuantityStock(new BigDecimal("-1.00"));
    var productInputDto = JsonUtil.toJson(product);

    var request = put(URL_PRODUCTS, product.getId())
        .contentType(APPLICATION_JSON)
        .content(productInputDto);
    mockMvc.perform(request)
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldReturnBadRequestWhenProductPriceIsLowerThanZero() throws Exception {
    var product = createProduct();
    updateProductAttributes(product);
    product.setPrice(new BigDecimal("-1.00"));
    var productInputDto = JsonUtil.toJson(product);

    var request = put(URL_PRODUCTS, product.getId())
        .contentType(APPLICATION_JSON)
        .content(productInputDto);
    mockMvc.perform(request)
        .andExpect(status().isBadRequest());
  }
}
