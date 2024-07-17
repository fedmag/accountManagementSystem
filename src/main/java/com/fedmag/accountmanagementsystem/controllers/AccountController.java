package com.fedmag.accountmanagementsystem.controllers;


import com.fedmag.accountmanagementsystem.common.dto.StatusUpdateDTO;
import com.fedmag.accountmanagementsystem.common.requests.PaymentRequest;
import com.fedmag.accountmanagementsystem.service.AccountService;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("api/acct")
public class AccountController {
  
  private final AccountService accountService;

  public AccountController(AccountService accountService) {
    this.accountService = accountService;
  }

  @PostMapping("/payments")
  public ResponseEntity<StatusUpdateDTO> postSalaries(@RequestBody List<PaymentRequest> paymentRequests) {
    log.info("postSalaries called: {}", paymentRequests);
    accountService.insertSalaries(paymentRequests);
    return ResponseEntity.ok(new StatusUpdateDTO("Added successfully!"));
  }

  @PutMapping("/payments")
  public ResponseEntity<StatusUpdateDTO> putSalary(@RequestBody PaymentRequest paymentRequest) {
    log.info("putSalary called: {}", paymentRequest);
    accountService.updateSalary(paymentRequest);
    return ResponseEntity.ok(new StatusUpdateDTO("Updated successfully!"));
  }
}
