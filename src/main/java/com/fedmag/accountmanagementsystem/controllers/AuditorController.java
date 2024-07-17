package com.fedmag.accountmanagementsystem.controllers;


import com.fedmag.accountmanagementsystem.common.dto.SecurityEventDTO;
import com.fedmag.accountmanagementsystem.service.AuditorService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/security")
public class AuditorController {

  private final AuditorService auditorService;

  @Autowired
  public AuditorController(AuditorService auditorService) {
    this.auditorService = auditorService;
  }

  @GetMapping("/events/")
  public List<SecurityEventDTO> getSecurityEvents() {
    return auditorService.getAllEvents();
  }
}

