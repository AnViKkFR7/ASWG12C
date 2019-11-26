package com.aswg12c.demo.exceptions;

public enum ExceptionMessages {
  EMAIL_IN_USE("This email addres is already in use"),
  USERNAME_IN_USE("This username is already in use"),
  INVALID_CREDENTIALS("Credentials are not correct. Try again"),
  ID_NOT_FOUND("There is no issue with that id"),
  NOT_ALL_PARAMETERS("You must complete all the required fields"),
  NOT_VALID_EMAIL("This email adress is not valid"),
  CONTENT_NOT_FOUND("This comment does not contain any text"),
  DIFFERENT_CREATOR("The creator of a comment can not be modified");
  private String message;

  ExceptionMessages(String message) {
    this.message = message;
  }

  public String getErrorMessage() {
    return message;
  }
}
