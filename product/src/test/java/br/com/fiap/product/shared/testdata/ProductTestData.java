package br.com.fiap.product.shared.testdata;

import br.com.fiap.product.domain.entity.Product;
import java.math.BigDecimal;
import java.util.UUID;

public final class ProductTestData {

  public static final UUID DEFAULT_PRODUCT_ID = UUID.randomUUID();
  public static final String DEFAULT_PRODUCT_ID_STRING = DEFAULT_PRODUCT_ID.toString();
  public static final String DEFAULT_PRODUCT_SKU = "Key/BR-/Erg/Gre";
  public static final String DEFAULT_PRODUCT_DESCRIPTION = "Keyboard Ergonomic Green";
  public static final String DEFAULT_PRODUCT_UNIT_MEASUREMENT = "UN";
  public static final BigDecimal DEFAULT_PRODUCT_QUANTITY_STOCK = new BigDecimal("10.00");
  public static final BigDecimal DEFAULT_PRODUCT_PRICE = new BigDecimal("315.00");
  public static final String DEFAULT_PRODUCT_IMAGE_URL = "https://m.media-amazon.com/images/I/71Yp7pxBFOL._AC_SX522_.jpg";
  public static final String ALTERNATIVE_PRODUCT_SKU = "Mou/BR-/Erg/Wht";
  public static final String ALTERNATIVE_PRODUCT_DESCRIPTION = "Mouse Ergonomic White";
  public static final String ALTERNATIVE_PRODUCT_UNIT_MEASUREMENT = "PC";
  public static final BigDecimal ALTERNATIVE_PRODUCT_QUANTITY_STOCK = new BigDecimal("15.00");
  public static final BigDecimal ALTERNATIVE_PRODUCT_PRICE = new BigDecimal("85.00");
  public static final String ALTERNATIVE_PRODUCT_IMAGE_URL = "https://m.media-amazon.com/images/I/719L-+P9vPL._AC_SX522_.jpg";

  public static Product createNewProduct() {
    return Product.builder()
        .active(true)
        .sku(DEFAULT_PRODUCT_SKU)
        .description(DEFAULT_PRODUCT_DESCRIPTION)
        .unitMeasurement(DEFAULT_PRODUCT_UNIT_MEASUREMENT)
        .quantityStock(DEFAULT_PRODUCT_QUANTITY_STOCK)
        .price(DEFAULT_PRODUCT_PRICE)
        .imageUrl(DEFAULT_PRODUCT_IMAGE_URL)
        .build();
  }

  public static Product createProduct() {
    var product = createNewProduct();
    product.setId(UUID.randomUUID());
    return product;
  }

  private ProductTestData() {
  }

  public static Product createAlternativeProduct() {
    var product = createNewAlternativeProduct();
    product.setId(UUID.randomUUID());
    return product;
  }

  public static Product createNewAlternativeProduct() {
    return Product.builder()
        .active(true)
        .sku(ALTERNATIVE_PRODUCT_SKU)
        .description(ALTERNATIVE_PRODUCT_DESCRIPTION)
        .unitMeasurement(ALTERNATIVE_PRODUCT_UNIT_MEASUREMENT)
        .quantityStock(ALTERNATIVE_PRODUCT_QUANTITY_STOCK)
        .price(ALTERNATIVE_PRODUCT_PRICE)
        .imageUrl(ALTERNATIVE_PRODUCT_IMAGE_URL)
        .build();
  }
}
