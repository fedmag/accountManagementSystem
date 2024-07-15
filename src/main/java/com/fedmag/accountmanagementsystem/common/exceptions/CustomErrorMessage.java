package com.fedmag.accountmanagementsystem.common.exceptions;

import java.time.LocalDateTime;

public record CustomErrorMessage(
  int status,
  LocalDateTime timestamp,
  String message,
  String error,
  String path){
}
