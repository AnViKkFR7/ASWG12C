package com.aswg12c.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "attachments")
public class Attachment {

  @Id
  @GeneratedValue
  private Long id;

  @JsonIgnore
  @Lob
  private byte[] attachment;

  @Column(unique = true)
  private String nom;

  @ManyToOne
  private Issue issue;

  public Attachment(){}

  public Attachment(byte[] attachment, String nom, Issue issue) {
    this.attachment = attachment;
    this.nom = nom;
    this.issue = issue;
  }

  public byte[] getAttachment() {
    return attachment;
  }

  public Issue getIssue() {
    return issue;
  }

  public void setIssue(Issue issue) {
    this.issue = issue;
  }

  public String getNom() {
    return nom;
  }

  public void setNom(String nom) {
    this.nom = nom;
  }

  public Long getId() {
    return id;
  }
}
