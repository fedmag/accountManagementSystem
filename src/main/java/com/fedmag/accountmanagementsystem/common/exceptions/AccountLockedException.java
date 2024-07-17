package com.fedmag.accountmanagementsystem.common.exceptions;

public class AccountLockedException extends RuntimeException {

  public AccountLockedException(String message) {
    super(message);
  }

}
