package br.com.fiap.order.infrastructure.configuration;

import br.com.fiap.order.infrastructure.httpclient.exception.CustomErrorDecoder;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClientConfiguration {

  @Bean
  public ErrorDecoder errorDecoder() {
    return new CustomErrorDecoder();
  }
}
