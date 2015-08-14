package com.andrewzurn.instagram.analyzer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableWebMvc
@EnableScheduling
@SpringBootApplication
public class InstagramAnalyzerApplication {

  public static void main(String[] args) {
        SpringApplication.run(InstagramAnalyzerApplication.class, args);
    }

}
