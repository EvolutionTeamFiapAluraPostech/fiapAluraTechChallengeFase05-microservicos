package br.com.fiap.payment.infrastructure.httpclient.order;

import br.com.fiap.payment.infrastructure.configuration.FeignConfiguration;
import br.com.fiap.payment.infrastructure.httpclient.order.dto.OrderDto;
import org.springframework.cloud.openfeign.FeignClient;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "order", url = "${base.url.http-order}", configuration = FeignConfiguration.class)
public interface OrderHttpClient {

  @GetMapping("/orders/{id}")
  ResponseEntity<OrderDto> getOrderById(@PathVariable String id);
}
