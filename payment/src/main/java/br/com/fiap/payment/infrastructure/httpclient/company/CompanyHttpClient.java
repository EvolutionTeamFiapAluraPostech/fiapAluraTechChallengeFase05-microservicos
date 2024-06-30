package br.com.fiap.payment.infrastructure.httpclient.company;

import br.com.fiap.payment.infrastructure.configuration.FeignConfiguration;
import br.com.fiap.payment.infrastructure.httpclient.company.dto.CompanyDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "company", url = "${base.url.http-company}", configuration = FeignConfiguration.class)
public interface CompanyHttpClient {

  @GetMapping(value = "/companies/{id}")
  ResponseEntity<CompanyDto> getCompanyById(@PathVariable String id);
}
