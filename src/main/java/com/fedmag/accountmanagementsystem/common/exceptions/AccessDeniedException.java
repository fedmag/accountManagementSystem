package com.fedmag.accountmanagementsystem.common.exceptions;

public class AccessDeniedException extends RuntimeException {

  public AccessDeniedException(String message) {
    super(message);
  }
}
