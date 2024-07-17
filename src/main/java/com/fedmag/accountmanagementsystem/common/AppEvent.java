package com.fedmag.accountmanagementsystem.common;

import com.fedmag.accountmanagementsystem.model.SecurityEventsEnum;
import java.time.LocalDateTime;
import org.springframework.context.ApplicationEvent;

public class AppEvent extends ApplicationEvent {
  private LocalDateTime date;
  private String eventString;
  private String subject;
  private String object;
  private String path;

  public AppEvent(Object source) {
    super(source);
  }

  public AppEvent(Object source, SecurityEventsEnum event, String subject, String object, String path) {
    super(source);
    this.date = LocalDateTime.now();
    this.eventString = event != null ? event.toString() : null;
    this.subject = subject; // who performs the action
    this.object = object; // who receives the action
    this.path = path;
  }

  public LocalDateTime getDate() {
    return date;
  }

  public void setDate(LocalDateTime date) {
    this.date = date;
  }

  public String getEventString() {
    return eventString;
  }

  public void setEventString(String eventString) {
    this.eventString = eventString;
  }

  public String getSubject() {
    return subject;
  }

  public void setSubject(String subject) {
    this.subject = subject;
  }

  public String getObject() {
    return object;
  }

  public void setObject(String object) {
    this.object = object;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }
}
