package br.com.fiap.payment.infrastructure.configuration;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextHolder;

@Configuration
public class FeignConfiguration {

  @Bean
  public RequestInterceptor requestInterceptor() {
    return template -> template.header("Authorization",
        "Bearer " + getJwtTokenFromSecurityContext());
  }

  private String getJwtTokenFromSecurityContext() {
    var authentication = SecurityContextHolder.getContext().getAuthentication();
    if ((authentication != null) && (authentication.getCredentials() instanceof String token)) {
      return token;
    }
    return null;
  }
}
