package com.aswg12c.demo.model;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Entity
@Table(
    name="session",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"userId"}), @UniqueConstraint(columnNames = {"id"})}
)
public class Session {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank
  private String userId;

  @NotBlank
  private String token;

  private Boolean loggedIn;

  Session(){
  }

  public Session(String userId, String token, Boolean loggedIn) {
    this.userId = userId;
    this.token = token;
    this.loggedIn = loggedIn;
  }
}
