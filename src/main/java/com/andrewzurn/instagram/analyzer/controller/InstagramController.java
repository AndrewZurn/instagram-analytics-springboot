package com.andrewzurn.instagram.analyzer.controller;

import com.andrewzurn.instagram.analyzer.service.InstagramService;
import com.sola.instagram.exception.InstagramException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.io.IOException;

/**
 * Created by andrew on 8/12/15.
 */
@RestController
@RequestMapping("/instagram")
public class InstagramController {

  @Inject
  private InstagramService instagramService;

  @RequestMapping(value = "/callback", method = RequestMethod.GET)
  public String callback(@RequestParam(value = "code") String urlCode) throws Exception {
    this.instagramService.finishSignon(urlCode);
    return "Finishing Instagram signon.";
  }
}
