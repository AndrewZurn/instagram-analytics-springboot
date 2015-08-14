package com.andrewzurn.instagram.analyzer.controller;

import com.andrewzurn.instagram.analyzer.model.SourceUser;
import com.andrewzurn.instagram.analyzer.service.CassandraService;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;

/**
 * Created by andrew on 8/10/15.
 */
@RestController
@RequestMapping("/user")
public class UserController {

  @Inject
  private CassandraService cassandraService;

  @RequestMapping(name = "/", method = RequestMethod.GET)
  public Iterable<SourceUser> all() {
    return cassandraService.getUsers();
  }

}
