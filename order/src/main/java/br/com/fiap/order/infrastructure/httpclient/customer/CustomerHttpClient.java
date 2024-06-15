package br.com.fiap.order.infrastructure.httpclient.customer;

import br.com.fiap.order.infrastructure.httpclient.customer.dto.CustomerDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "customer", url = "${base.url.http-customer}")
public interface CustomerHttpClient {

  @GetMapping("/customers/{id}")
  ResponseEntity<CustomerDto> getCustomerById(@PathVariable String id);
}
