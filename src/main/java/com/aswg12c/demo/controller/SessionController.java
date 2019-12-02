package com.aswg12c.demo.controller;

import com.aswg12c.demo.exceptions.GenericException;
import com.aswg12c.demo.model.Session;
import com.aswg12c.demo.repository.SessionRepository;
import java.security.GeneralSecurityException;
import javax.servlet.annotation.HttpConstraint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api" + SessionController.PATH)
public class SessionController {
  public static final String PATH = "/sessions";

  @Autowired
  private SessionRepository sessionRepository;

  /* Este està repetido en el AswApplication, queda oculto
  @PutMapping("/logout")
  @ResponseStatus(HttpStatus.OK)
  */
  /*
  void logOut(@RequestParam(name = "token") String facebook_token){
    Session actual_session = sessionRepository.findByToken(facebook_token);

    if (actual_session == null) throw new GenericException(HttpStatus.BAD_REQUEST, "There is no session with that token");

    if (actual_session.getLoggedIn()) sessionRepository.deleteByUserId(actual_session.getUserId());
    else throw new GenericException(HttpStatus.BAD_REQUEST, "This user is already logged out");
  }

   */
}