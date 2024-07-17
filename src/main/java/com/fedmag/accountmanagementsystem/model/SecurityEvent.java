package com.fedmag.accountmanagementsystem.model;

import com.fedmag.accountmanagementsystem.common.AppEvent;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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
