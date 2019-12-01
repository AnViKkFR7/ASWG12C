package com.aswg12c.demo.controller;

import com.aswg12c.demo.repository.CommentRepository;
import com.aswg12c.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api" + UserController.PATH)
public class UserController {
  public static final String PATH = "/users";

  @Autowired
  private UserRepository userRepository;



  /*
  Pattern patronEmail = Pattern.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)(\\.[A-Za-z]{2,})$");
    Matcher mEmail = patronEmail.matcher(email.toLowerCase());
    if (!mEmail.matches()){
      throw new GenericException(HttpStatus.BAD_REQUEST,
          ExceptionMessages.NOT_VALID_EMAIL.getErrorMessage());
    }
   */
}

