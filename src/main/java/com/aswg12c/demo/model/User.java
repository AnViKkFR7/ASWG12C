package com.aswg12c.demo.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotBlank;

import org.checkerframework.common.aliasing.qual.Unique;
import org.springframework.web.multipart.MultipartFile;

@Entity
@Table(
    name="users",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"id"}), @UniqueConstraint(columnNames = {"facebookId"})}

)
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank
  private String username;

  @NotBlank
  private String facebookId;

  @NotBlank
  private String facebookToken;

  //private MultipartFile photo;

  User(){
  }

  public User(String username, String facebookId, String facebookToken){
    this.username = username;
    this.facebookId = facebookId;
    this.facebookToken = facebookToken;
  }

  /**
   * Setters and Getters
   */

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getFacebookId() {
    return facebookId;
  }

  public void setFacebookId(String facebookId) {
    this.facebookId = facebookId;
  }

  public String getFacebookToken() {
    return facebookToken;
  }

  public void setFacebookToken(String facebookToken) {
    this.facebookToken = facebookToken;
  }
}
