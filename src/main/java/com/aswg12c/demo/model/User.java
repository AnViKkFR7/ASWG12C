package com.aswg12c.demo.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotBlank;
import org.springframework.web.multipart.MultipartFile;

@Entity
@Table(
    name="users",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"id", "username", "mail"})}

)
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank
  private String username;

  private String password;

  @NotBlank
  private String mail;

  private String google_token;

  private String google_refresh_token;

  //private MultipartFile photo;

  User(){
  }

  public User(String username, String password, String mail, String g_t, String g_t_r){
    this.username = username;
    this.password = password;
    this.mail = mail;
    this.google_token = g_t;
    this.google_refresh_token = g_t_r;
  }

  /**
   * Setters and Getters
   */

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getMail() {
    return mail;
  }

  public void setMail(String mail) {
    this.mail = mail;
  }

  public String getGoogle_token() {
    return google_token;
  }

  public void setGoogle_token(String google_token) {
    this.google_token = google_token;
  }

  public String getGoogle_refresh_token() {
    return google_refresh_token;
  }

  public void setGoogle_refresh_token(String google_refresh_token) {
    this.google_refresh_token = google_refresh_token;
  }
}
