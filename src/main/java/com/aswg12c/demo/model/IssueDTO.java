package com.aswg12c.demo.model;

public class IssueDTO {
  private String title;
  private kindEnum kind;
  private priorityEnum priority;
  private String description;
  private statusEnum status;
  private String comment;

  IssueDTO(){}

  public IssueDTO(String title, kindEnum kind, priorityEnum priority, String description, String comment){
    this.title = title;
    this.kind = kind;
    this.priority = priority;
    this.description = description;
    this.comment = comment;
  }


  public String getTitle() {
    return title;
  }

  public kindEnum getKind() {
    return kind;
  }

  public priorityEnum getPriority() {
    return priority;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public statusEnum getStatus() {
    return status;
  }

  public String getComment() {
    return comment;
  }
}
