package com.fedmag.accountmanagementsystem.controllers;


import com.fedmag.accountmanagementsystem.common.dto.EmailStatusUpdateDTO;
import com.fedmag.accountmanagementsystem.common.dto.UserDTO;
import com.fedmag.accountmanagementsystem.common.requests.ChangePassRequest;
import com.fedmag.accountmanagementsystem.common.requests.RegistrationRequest;
import com.fedmag.accountmanagementsystem.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final static Logger log = LoggerFactory.getLogger(AuthController.class);

  private final AuthService authService;

  @Autowired
  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @PostMapping("/signup")
  public ResponseEntity<UserDTO> signup(
      @RequestBody RegistrationRequest registrationRequest) {

    log.info("signup endpoint called. Param: {}", registrationRequest.toString());
    UserDTO userDTO = authService.registerUser(registrationRequest);
    return ResponseEntity.ok().body(userDTO);
  }

  @PostMapping("/changepass")
  public ResponseEntity<EmailStatusUpdateDTO> changePass(
      @AuthenticationPrincipal UserDetails userDetails,
      @RequestBody ChangePassRequest passRequest) {

    log.info("changepass endpoint called. Param: {}", passRequest.toString());
    return ResponseEntity.ok().body(authService.changePass(userDetails, passRequest));
  }
}
