package com.andrewzurn.instagram.analyzer.controller;

import com.andrewzurn.instagram.analyzer.model.SourceUser;
import com.andrewzurn.instagram.analyzer.service.CassandraService;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.xml.transform.Source;
import java.util.Iterator;
import java.util.List;

/**
 * Created by andrew on 8/10/15.
 */
@RestController
@RequestMapping("/user")
public class UserController {

  @Inject
  private CassandraService cassandraService;

  @RequestMapping(method = RequestMethod.GET)
  public Iterable<SourceUser> all() {
    return this.cassandraService.getUsers();
  }

  @RequestMapping(value = "/{userId}", method = RequestMethod.GET)
  public SourceUser findByUserId(@PathVariable int userId) {
    return this.cassandraService.findByUserId(userId);
  }

  @RequestMapping(value = "/traversed/{hasBeenTraversed}", method = RequestMethod.GET)
  public List<SourceUser> findByTraversedUserFollows(@PathVariable boolean hasBeenTraversed) {
    return this.cassandraService.findByTraversedUserFollows(hasBeenTraversed);
  }

  @RequestMapping(value = "/traversed/single", method = RequestMethod.GET)
  public SourceUser findSingleUntraversedUser() {
    return this.cassandraService.findSingleUntraversedFollowsUser();
  }

}
