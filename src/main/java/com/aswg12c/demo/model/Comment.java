package com.aswg12c.demo.model;


import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name="comment")
public class Comment {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank
  private String content;

  @ManyToOne
  private User creator;

  @ManyToOne
  private Issue issueCommented;

  private Date creationDate;

  Comment(){}

  public Comment(String content, User creator, Issue issueCommented, Date creationDate){
    this.content = content;
    this.creationDate = creationDate;
    this.creator = creator;
    this.issueCommented = issueCommented;
  }

  /**
   * Setters & Getters
   */

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public User getCreator() {
    return creator;
  }

  public void setCreator(User creator) {
    this.creator = creator;
  }

  public Issue getIssueCommented() {
    return issueCommented;
  }

  public void setIssueCommented(Issue issueCommented) {
    this.issueCommented = issueCommented;
  }

  public Date getCreationDate() {
    return creationDate;
  }

  public void setCreationDate(Date creationDate) {
    this.creationDate = creationDate;
  }


  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }
}
