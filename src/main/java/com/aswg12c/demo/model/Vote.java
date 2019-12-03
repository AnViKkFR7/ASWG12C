package com.aswg12c.demo.model;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

@Entity
@Table(name="votes")
public class Vote {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  private Issue issue;

  @ManyToOne
  private User user;

  public Vote(){}

  public Vote(Issue issue, User user) {
    this.issue = issue;
    this.user = user;
  }

  public User getUser() {
    return user;
  }

  public Issue getIssue() {
    return issue;
  }
}
