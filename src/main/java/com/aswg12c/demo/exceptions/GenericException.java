package com.aswg12c.demo.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class GenericException extends ResponseStatusException {

  public GenericException(HttpStatus status, String message) {
    super(status, message);
  }

}

