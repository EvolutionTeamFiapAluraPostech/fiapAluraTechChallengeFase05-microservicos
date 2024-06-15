package br.com.fiap.order.infrastructure.httpclient.logistics;

import br.com.fiap.order.infrastructure.httpclient.logistics.dto.LogisticOrderDto;
import br.com.fiap.order.infrastructure.httpclient.logistics.dto.LogisticOrderInputDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "logistic", url = "${base.url.http-logistic}")
public interface LogisticsClient {

  @PostMapping("/logistics")
  ResponseEntity<LogisticOrderDto> postOrderLogistics(
      @RequestBody LogisticOrderInputDto logisticOrderInputDto);

}
