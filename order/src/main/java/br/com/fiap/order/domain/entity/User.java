package br.com.fiap.order.domain.entity;

import java.util.List;
import org.springframework.security.core.GrantedAuthority;

public class User {

  private String id;
  private String name;
  private String sub;
  private String iss;
  private Long exp;
  private List<GrantedAuthority> authorities;

  public User() {
  }

  public User(String id, String name, String sub, String iss, Long exp,
      List<GrantedAuthority> authorities) {
    this.id = id;
    this.name = name;
    this.sub = sub;
    this.iss = iss;
    this.exp = exp;
    this.authorities = authorities;
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getSub() {
    return sub;
  }

  public String getIss() {
    return iss;
  }

  public Long getExp() {
    return exp;
  }

  public List<GrantedAuthority> getAuthorities() {
    return authorities;
  }
}
