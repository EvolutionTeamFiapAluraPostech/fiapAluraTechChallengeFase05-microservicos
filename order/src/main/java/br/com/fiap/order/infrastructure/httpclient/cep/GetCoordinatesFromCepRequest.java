package br.com.fiap.order.infrastructure.httpclient.cep;

import br.com.fiap.order.OrderApplication;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
public class GetCoordinatesFromCepRequest {

  public static final String SMALL_X_Y_Z_A_HREF_HTTPS_GEOCODE_XYZ = "<small>x,y z: <a href=\"https://geocode.xyz/";
  public static final String SMALL_X_Y_Z = "<small>x,y z:";
  public static final String URL_GEOCODE = "https://geocode.xyz";

  public List<Map<String, BigDecimal>> request(String cepParam) {
    var cep = cepParam.substring(0, 5) + "-" + cepParam.substring(5);
    var coordinates = getCoordinatesFromWebByCep(cep);
    if (StringUtils.hasLength(coordinates)) {
      var commaPosition = coordinates.indexOf(",");
      var latitude = coordinates.substring(0, commaPosition - 1);
      var longitude = coordinates.substring(commaPosition + 1);
      var coordinatesMapList = new ArrayList<Map<String, BigDecimal>>();
      Map<String, BigDecimal> coordinatesMap = new HashMap<>();
      coordinatesMap.put("Latitude", new BigDecimal(latitude));
      coordinatesMap.put("Longitude", new BigDecimal(longitude));
      coordinatesMapList.add(coordinatesMap);
      return coordinatesMapList;
    }
    return Collections.emptyList();
  }

  private String getCoordinatesFromWebByCep(String cep) {
    try {
      var client = WebClient.builder()
          .baseUrl(URL_GEOCODE)
          .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_HTML_VALUE)
          .build();
      var plainTextReturnedFromRequest = client.get().uri("/{cep}?region=BR", cep)
          .retrieve()
          .bodyToMono(String.class).block();
      if (StringUtils.hasLength(plainTextReturnedFromRequest)
          && plainTextReturnedFromRequest.contains(SMALL_X_Y_Z_A_HREF_HTTPS_GEOCODE_XYZ)) {
        var initialIndex = plainTextReturnedFromRequest.indexOf(SMALL_X_Y_Z);
        var lastIndex = initialIndex + 64;
        var coordinates = plainTextReturnedFromRequest.substring(initialIndex, lastIndex);
        return getCoordinates(coordinates);
      }
    } catch (WebClientResponseException e) {
      handlingExceptionFromGettingCoordinatesByCep(cep, e);
    }
    return null;
  }

  private void handlingExceptionFromGettingCoordinatesByCep(String cep,
      WebClientResponseException e) {
    var error = this.getClass().getSimpleName() + "-getCoordinatesFromWebByCep(String cep) ";
    if (e.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
      error += "Coordinates not found for zip code %s".formatted(cep);
    } else {
      error += e.getStatusCode().value() + "-" + e.getStatusText();
    }
    OrderApplication.logger.error(error);
  }

  private String getCoordinates(String texto) {
    var pattern = Pattern.compile("-?\\d+(\\.\\d+)?");
    var matcher = pattern.matcher(texto);
    var numbersFound = new StringBuilder();
    while (matcher.find()) {
      if (StringUtils.hasLength(numbersFound)) {
        numbersFound.append(",");
      }
      numbersFound.append(matcher.group());
    }
    return numbersFound.toString().trim();
  }
}
