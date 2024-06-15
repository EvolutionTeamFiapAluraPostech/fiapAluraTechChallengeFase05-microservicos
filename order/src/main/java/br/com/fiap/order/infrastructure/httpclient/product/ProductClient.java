package br.com.fiap.order.infrastructure.httpclient.product;

import br.com.fiap.order.infrastructure.httpclient.product.dto.ProductDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "product", url = "${base.url.http-product}")
public interface ProductClient {

  @GetMapping("/products/{id}")
  ResponseEntity<ProductDto> getProductById(@PathVariable String id);
}
