package com.aswg12c.demo.model;

public class CommentDTO {
  private String content;
  private Long issueId;

  CommentDTO(){}

  public CommentDTO(String content, Long iid){
    this.content = content;
    this.issueId = iid;
  }

  public String getContent() {
    return content;
  }

  public Long getIssueId() {
    return issueId;
  }
}
