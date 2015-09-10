package com.andrewzurn.instagram.analyzer.controller;

import com.andrewzurn.instagram.analyzer.model.SourceUser;
import com.andrewzurn.instagram.analyzer.service.CassandraService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Source;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.StreamSupport;

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

  @RequestMapping(value = "/export", method = RequestMethod.GET)
  @ResponseBody
  public void exportAsTsvString(HttpServletResponse response) throws IOException {
    String returnString = SourceUser.tsvHeader();
    Iterable<SourceUser> users = this.cassandraService.getUsers();
    for (SourceUser user : users) {
      returnString += user.tsvRepr();
    }
    response.setHeader("Content-Disposition", "attachment; filename=user_export.txt");
    response.setContentType("application/txt");
    response.getWriter().write(returnString);
    response.flushBuffer();
  }

  @RequestMapping(value = "/clean", method = RequestMethod.GET)
  public String clean() {
    Iterable<SourceUser> users = this.cassandraService.getUsers();
    for (SourceUser user : users) {
      user.setBio(user.getBio().replaceAll("\n", ""));
      user.setWebsite(user.getWebsite().replaceAll("\n", ""));
      user.setUserName(user.getUserName().replaceAll("\n", ""));
      user.setFullName(user.getFullName().replaceAll("\n", ""));
      user.setBio(user.getBio().replaceAll("\t", ""));
      user.setWebsite(user.getWebsite().replaceAll("\t", ""));
      user.setUserName(user.getUserName().replaceAll("\t", ""));
      user.setFullName(user.getFullName().replaceAll("\t", ""));
      user.setBio(user.getBio().replaceAll("\r", ""));
      user.setWebsite(user.getWebsite().replaceAll("\r", ""));
      user.setUserName(user.getUserName().replaceAll("\r", ""));
      user.setFullName(user.getFullName().replaceAll("\r", ""));
      user.setBio(user.getBio().replaceAll("|", ""));
      user.setWebsite(user.getWebsite().replaceAll("|", ""));
      user.setUserName(user.getUserName().replaceAll("|", ""));
      user.setFullName(user.getFullName().replaceAll("|", ""));
      List<String> locations = new ArrayList();
      try {
        for (String loc : user.getLocations()) {
          if (!loc.equals("")) {
            locations.add(loc.replaceAll("\n", ""));
            locations.add(loc.replaceAll("\r", ""));
            locations.add(loc.replaceAll("\t", ""));
            locations.add(loc.replaceAll("|", ""));
          }
        }
      } catch (NullPointerException ne) { }
      user.setLocations(locations);
      cassandraService.saveUser(user);
    }
    return "done";
  }

  @RequestMapping(value = "/traversed/single", method = RequestMethod.GET)
  public SourceUser findSingleUntraversedUser() {
    return this.cassandraService.findSingleUntraversedFollowsUser();
  }

}
