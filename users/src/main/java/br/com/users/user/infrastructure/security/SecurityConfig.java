package br.com.users.user.infrastructure.security;

import static br.com.users.user.infrastructure.security.UserRole.ADMIN;
import static br.com.users.user.infrastructure.security.UserRole.USER;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.password.CompromisedPasswordChecker;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.password.HaveIBeenPwnedRestApiPasswordChecker;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

  public static final String URL_USERS = "/users";
  public static final String URL_AUTHENTICATE = "/authenticate";
  public static final String V3_API_DOCS = "/v3/api-docs/**";
  public static final String SWAGGER_UI_HTML = "/swagger-ui.html";
  public static final String SWAGGER_UI = "/swagger-ui/**";
  private final SecurityFilter securityFilter;

  public SecurityConfig(SecurityFilter securityFilter) {
    this.securityFilter = securityFilter;
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http.csrf(AbstractHttpConfigurer::disable)
        .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(req -> {
          req.requestMatchers(POST, URL_AUTHENTICATE).permitAll();
          req.requestMatchers(POST, URL_USERS).permitAll();
          req.requestMatchers(GET, URL_USERS + "/**").hasAnyAuthority(USER.name(), ADMIN.name());
          req.requestMatchers(PUT, URL_USERS + "/**").hasAuthority(ADMIN.name());
          req.requestMatchers(DELETE, URL_USERS + "/**").hasAuthority(ADMIN.name());
          req.requestMatchers(V3_API_DOCS, SWAGGER_UI_HTML, SWAGGER_UI).permitAll();
          req.anyRequest().denyAll();
        })
        .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
        .build();
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)
      throws Exception {
    return configuration.getAuthenticationManager();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public CompromisedPasswordChecker compromisedPasswordChecker() {
    return new HaveIBeenPwnedRestApiPasswordChecker();
  }
}
