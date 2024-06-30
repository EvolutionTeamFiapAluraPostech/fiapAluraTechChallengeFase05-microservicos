package br.com.fiap.payment.infrastructure.httpclient.customer;

import br.com.fiap.payment.infrastructure.configuration.FeignConfiguration;
import br.com.fiap.payment.infrastructure.httpclient.customer.dto.CustomerDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "customer", url = "${base.url.http-user}", configuration = FeignConfiguration.class)
public interface CustomerHttpClient {

  @GetMapping("/users/{id}")
  ResponseEntity<CustomerDto> getCustomerById(@PathVariable String id);
}
