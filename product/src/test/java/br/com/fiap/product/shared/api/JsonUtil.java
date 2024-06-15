package br.com.fiap.product.shared.api;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.util.Objects;

public class JsonUtil {

  private static final ObjectMapper mapper;

  static {
    mapper = new ObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .configure(DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS, false)
        .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
        .setSerializationInclusion(Include.NON_NULL)
        .enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)
        .findAndRegisterModules();
  }

  private JsonUtil() {
    throw new IllegalStateException("Utility class");
  }

  public static ObjectMapper getMapper() {
    return mapper;
  }

  public static String toJson(final Object data) {
    try {
      return mapper.writeValueAsString(data);
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }

  public static <A> A fromJson(String json, Class<A> clazz) {
    Objects.requireNonNull(json, "Value cannot be null.");
    try {
      return mapper.readValue(json, clazz);
    } catch (Exception e) {
      throw new IllegalStateException(e.getMessage());
    }
  }

  public static <A> A fromJson(Object fromValue, Class<A> clazz) {
    Objects.requireNonNull(fromValue, "Value cannot be null.");
    return mapper.convertValue(fromValue, clazz);
  }
}
