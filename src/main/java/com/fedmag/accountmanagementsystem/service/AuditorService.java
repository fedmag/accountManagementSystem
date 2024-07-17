package com.fedmag.accountmanagementsystem.service;


import com.fedmag.accountmanagementsystem.common.dto.SecurityEventDTO;
import com.fedmag.accountmanagementsystem.model.SecurityEvent;
import com.fedmag.accountmanagementsystem.model.SecurityEventRepo;
import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuditorService {

  private final SecurityEventRepo securityEventRepo;
  private final HttpServletRequest request;

  @Autowired
  public AuditorService(SecurityEventRepo securityEventRepo, HttpServletRequest request) {
    this.securityEventRepo = securityEventRepo;
    this.request = request;
  }

  public List<SecurityEventDTO> getAllEvents() {
    log.info("--> fetching all events. Requesting user %s".formatted(request.getRemoteUser()));
    Iterable<SecurityEvent> securityEvents = securityEventRepo.findAll();
    List<SecurityEventDTO> eventDTOs = new ArrayList<>();
    for (SecurityEvent event : securityEvents) {
      eventDTOs.add(SecurityEventDTO.from(event));
    }
    return eventDTOs;
  }
}
