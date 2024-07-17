package com.fedmag.accountmanagementsystem.controllers;


import com.fedmag.accountmanagementsystem.common.exceptions.BadRequestException;
import com.fedmag.accountmanagementsystem.common.exceptions.CustomErrorMessage;
import com.fedmag.accountmanagementsystem.common.exceptions.NotFoundException;
import com.fedmag.accountmanagementsystem.common.exceptions.UnauthenticatedException;
import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class ExceptionsController {

  @ExceptionHandler(value = {BadRequestException.class})
  public ResponseEntity<CustomErrorMessage> handleBadRequestException(BadRequestException e,
      WebRequest request) {

    CustomErrorMessage body = new CustomErrorMessage(
        HttpStatus.BAD_REQUEST.value(),
        LocalDateTime.now(),
        e.getMessage(),
        "Bad Request",
        formatPath(request.getDescription(false)));

    return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(value = {UnauthenticatedException.class})
  public ResponseEntity<CustomErrorMessage> handleUnauthenticatedException(UnauthenticatedException e,
      WebRequest request) {

    CustomErrorMessage body = new CustomErrorMessage(
        HttpStatus.UNAUTHORIZED.value(),
        LocalDateTime.now(),
        e.getMessage(),
        "Bad Request",
        formatPath(request.getDescription(false)));

    return new ResponseEntity<>(body, HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(value = {AccessDeniedException.class})
  public ResponseEntity<CustomErrorMessage> handleAccessDeniedException(AccessDeniedException e,
      WebRequest request) {

    CustomErrorMessage body = new CustomErrorMessage(
        HttpStatus.FORBIDDEN.value(),
        LocalDateTime.now(),
        e.getMessage(),
        "Forbidden",
        formatPath(request.getDescription(false)));

    return new ResponseEntity<>(body, HttpStatus.FORBIDDEN);
  }

  @ExceptionHandler(value = {NotFoundException.class})
  public ResponseEntity<CustomErrorMessage> handleNotFoundException(NotFoundException e,
      WebRequest request) {

    CustomErrorMessage body = new CustomErrorMessage(
        HttpStatus.NOT_FOUND.value(),
        LocalDateTime.now(),
        e.getMessage(),
        "Not Found",
        formatPath(request.getDescription(false)));

    return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
  }

  private String formatPath(String uri) {
    return uri.replace("uri=", "");
  }
}
