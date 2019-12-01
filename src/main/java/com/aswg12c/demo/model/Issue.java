package com.aswg12c.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
@Table(name="issue")
public class Issue {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank
  private String title;

  private String description;

  @NotNull
  private kindEnum kind;

  @NotNull
  private priorityEnum priority;

  private statusEnum status;

  private int votes;

  private int watchers;

  @NotNull
  private Date creationDate;

  @NotNull
  private Date updatedDate;

  @JsonIgnore
  @Lob
  private byte[] image;

  @ManyToOne
  @JoinColumn(name = "creator")
  private User creator;

  public Issue(){}

  public Issue (String title, String description, kindEnum kind,
      priorityEnum priority, statusEnum status, int votes,
      int watchers, Date creationDate, Date updatedDate, User creator){
    this.title = title;
    this.creationDate = creationDate;
    this.creator = creator;
    this.priority = priority;
    this.kind = kind;
    this.status = status;
    this.updatedDate = updatedDate;
    this.description = description;
    this.watchers = watchers;
    this.votes = votes;
  }

  /**
   * Setters and getters
   */

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }


  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public kindEnum getKind() {
    return kind;
  }

  public void setKind(kindEnum kind) {
    this.kind = kind;
  }

  public priorityEnum getPriority() {
    return priority;
  }

  public void setPriority(priorityEnum priority) {
    this.priority = priority;
  }

  public statusEnum getStatus() {
    return status;
  }

  public void setStatus(statusEnum status) {
    this.status = status;
  }

  public int getVotes() {
    return votes;
  }

  public void setVotes(int votes) {
    this.votes = votes;
  }

  public int getWatchers() {
    return watchers;
  }

  public void setWatchers(int watchers) {
    this.watchers = watchers;
  }

  public Date getCreationDate() {
    return creationDate;
  }

  public void setCreationDate(Date creationDate) {
    this.creationDate = creationDate;
  }

  public Date getUpdatedDate() {
    return updatedDate;
  }

  public void setUpdatedDate(Date updatedDate) {
    this.updatedDate = updatedDate;
  }

  public User getCreator() {
    return creator;
  }

  public void setCreator(User creator) {
    this.creator = creator;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public byte[] getImage() {
    return image;
  }

  public void setImage(byte[] image) {
    this.image = image;
  }
}
