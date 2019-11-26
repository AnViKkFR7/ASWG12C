package com.aswg12c.demo.model;

public class CommentDTO {
  private String content;
  private Long userId;
  private Long issueId;

  CommentDTO(){}

  public CommentDTO(String content, Long uid, Long iid){
    this.content = content;
    this.userId = uid;
    this.issueId = iid;
  }

  public String getContent() {
    return content;
  }

  public Long getUserId() {
    return userId;
  }

  public Long getIssueId() {
    return issueId;
  }
}
