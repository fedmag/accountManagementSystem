package com.fedmag.accountmanagementsystem.model;

import com.fedmag.accountmanagementsystem.common.AppEvent;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "security_events")
public class SecurityEvent {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;
  private LocalDateTime date;
  private String eventString;
  @Column(name = "username")
  private String subject;
  private String object;
  private String path;

  public SecurityEvent() {}

  public SecurityEvent(LocalDateTime date, String eventString, String subject, String object,
      String path) {
    this.date = date;
    this.eventString = eventString;
    this.subject = subject;
    this.object = object;
    this.path = path;
  }

  public long getId() {
    return id;
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

  public static SecurityEvent from(AppEvent event) {
    SecurityEvent securityEvent = new SecurityEvent();
    securityEvent.setDate(event.getDate());
    securityEvent.setPath(event.getPath());
    securityEvent.setSubject(event.getSubject());
    securityEvent.setObject(event.getObject());
    securityEvent.setEventString(event.getEventString());
    return securityEvent;
  }
}
