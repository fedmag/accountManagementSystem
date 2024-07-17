package com.fedmag.accountmanagementsystem.common.exceptions;

public class AccessDeniedException extends
    org.springframework.security.access.AccessDeniedException {

  public AccessDeniedException(String message) {
    super(message);
  }
}
