package com.aswg12c.demo.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotBlank;

@Entity
@Table(
    name="session"
    //uniqueConstraints = {@UniqueConstraint(columnNames = {"id_authentication", "userSession"})}
)
public class Session {

  @Id
  private Long id_authentication;

  @OneToOne
  @NotBlank
  private User userSession;

  Session(){}
}
