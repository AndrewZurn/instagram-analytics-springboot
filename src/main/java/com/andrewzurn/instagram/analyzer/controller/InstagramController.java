package com.andrewzurn.instagram.analyzer.controller;

import com.andrewzurn.instagram.analyzer.service.InstagramService;
import com.sola.instagram.exception.InstagramException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

  private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

  @Inject
  private InstagramService instagramService;

  @RequestMapping(method = RequestMethod.GET)
  public String startMining() throws InstagramException, IOException {
    if ( !instagramService.isReady()) {
      String authUrl = this.instagramService.startSignon();
      LOGGER.info("OAuth instagram request with url: {}", authUrl);
      return "Please open on the following link to finish signon:<br/>\n" +
          "<a href='"+ authUrl + "'>Start Instagram Mining</a>";
    } else {
      return "Mining already in progress";
    }
  }

  @RequestMapping(value = "/callback", method = RequestMethod.GET)
  public String callback(@RequestParam(value = "code") String urlCode) throws Exception {
    LOGGER.info("Finishing OAuth signon with code: {}", urlCode);
    this.instagramService.finishSignon(urlCode);
    return "Finishing Instagram signon.";
  }
}
