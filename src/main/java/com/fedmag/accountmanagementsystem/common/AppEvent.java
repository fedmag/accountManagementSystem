package com.fedmag.accountmanagementsystem.common;

import com.fedmag.accountmanagementsystem.model.SecurityEventsEnum;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Setter
@Getter
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

}
