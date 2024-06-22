package br.com.fiap.order.infrastructure.httpclient.company;

import br.com.fiap.order.infrastructure.httpclient.company.dto.CompanyDto;
import br.com.fiap.order.infrastructure.configuration.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "company", url = "${base.url.http-company}", configuration = FeignConfig.class)
public interface CompanyHttpClient {

  @GetMapping(value = "/companies/{id}")
  ResponseEntity<CompanyDto> getCompanyById(@PathVariable String id);
}
