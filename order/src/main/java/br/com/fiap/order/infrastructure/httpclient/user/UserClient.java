package br.com.fiap.order.infrastructure.httpclient.user;

import br.com.fiap.order.infrastructure.httpclient.user.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "user", url = "${base.url.http-user}")
public interface UserClient {

  @GetMapping("/users/{id}")
  ResponseEntity<UserDto> getUserById(@PathVariable String id);

}
