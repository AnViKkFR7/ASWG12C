package com.aswg12c.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api" + SessionController.PATH)
public class SessionController {
  public static final String PATH = "/sessions";

  @Autowired
  private SessionController sessionRepository;
}