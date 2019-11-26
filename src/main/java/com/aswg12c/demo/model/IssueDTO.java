package com.aswg12c.demo.model;

public class IssueDTO {
  private String title;
  private kindEnum kind;
  private priorityEnum priority;
  private Long userCreatorId;

  IssueDTO(){}

  public IssueDTO(String title, kindEnum kind, priorityEnum priority, Long userCreatorId){
    this.title = title;
    this.kind = kind;
    this.priority = priority;
    this.userCreatorId = userCreatorId;
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

  public Long getUserCreatorId() {
    return userCreatorId;
  }
}
