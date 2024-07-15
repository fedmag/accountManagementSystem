package com.fedmag.accountmanagementsystem.controllers;


import com.fedmag.accountmanagementsystem.service.EmployeeService;
import jakarta.websocket.server.PathParam;
import java.time.YearMonth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/empl")
public class EmployeeController {

  private final static Logger LOG = LoggerFactory.getLogger(EmployeeController.class);

  private final EmployeeService employeeService;

  @Autowired
  public EmployeeController(EmployeeService employeeService) {
    this.employeeService = employeeService;
  }

  @GetMapping("/payment")
  public ResponseEntity<?> getPayment(@AuthenticationPrincipal UserDetails details,
      @PathParam("period") @DateTimeFormat(pattern = "MM-yyyy") YearMonth period) {
    LOG.error("getPayment endpoint was called.");
    if (period == null) {
      return ResponseEntity.ok().body(employeeService.getAllPayments(details.getUsername()));
    }
    return ResponseEntity.ok().body(employeeService.getPayment(details.getUsername(), period));
  }

}
