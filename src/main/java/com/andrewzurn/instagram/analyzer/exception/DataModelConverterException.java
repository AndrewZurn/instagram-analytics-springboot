package com.andrewzurn.instagram.analyzer.exception;

/**
 * Created by andrew on 8/16/15.
 */
public class DataModelConverterException extends Exception {
  public DataModelConverterException() {
    super();
  }

  public DataModelConverterException(String message) {
    super(message);
  }

  public DataModelConverterException(String message, Throwable cause) {
    super(message, cause);
  }

  public DataModelConverterException(Throwable cause) {
    super(cause);
  }

  protected DataModelConverterException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
