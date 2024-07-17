package com.fedmag.accountmanagementsystem.common.dto;

import com.fedmag.accountmanagementsystem.model.SecurityEvent;
import java.time.LocalDateTime;

public class SecurityEventDTO {

  private long id;
  private LocalDateTime date;
  private String action;
  private String subject;
  private String object;
  private String path;

  public static SecurityEventDTO from(SecurityEvent event) {
    SecurityEventDTO dto = new SecurityEventDTO();
    dto.date = event.getDate();
    dto.id = event.getId();
    dto.action = event.getEventString();
    dto.subject = event.getSubject();
    dto.object = event.getObject();
    dto.path = event.getPath();
    return dto;
  }

  public long getId() {
    return id;
  }

  public LocalDateTime getDate() {
    return date;
  }

  public String getAction() {
    return action;
  }

  public String getSubject() {
    return subject;
  }

  public String getObject() {
    return object;
  }

  public String getPath() {
    return path;
  }
}
