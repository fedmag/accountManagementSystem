package com.fedmag.accountmanagementsystem.common.dto;

import com.fedmag.accountmanagementsystem.model.SecurityEvent;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
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

}
