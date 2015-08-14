package com.andrewzurn.instagram.analyzer.exception;

/**
 * Created by andrew on 8/11/15.
 */
public class InstagramApiException extends Exception {

  public InstagramApiException(String message) {
    super(message);
  }

  public InstagramApiException(String message, Throwable throwable) {
    super(message, throwable);
  }

}
