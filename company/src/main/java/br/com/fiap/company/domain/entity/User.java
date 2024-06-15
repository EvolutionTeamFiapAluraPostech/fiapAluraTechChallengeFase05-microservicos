package br.com.fiap.company.domain.entity;

public class User {
  private String id;
  private String name;
  private String sub;
  private String iss;
  private Long exp;

  public User() {
  }

  public User(String id, String name, String sub, String iss, Long exp) {
    this.id = id;
    this.name = name;
    this.sub = sub;
    this.iss = iss;
    this.exp = exp;
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
}
