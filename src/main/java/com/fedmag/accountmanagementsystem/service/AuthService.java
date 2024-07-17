package com.fedmag.accountmanagementsystem.service;


import com.fedmag.accountmanagementsystem.common.AppEvent;
import com.fedmag.accountmanagementsystem.common.dto.EmailStatusUpdateDTO;
import com.fedmag.accountmanagementsystem.common.RolesEnum;
import com.fedmag.accountmanagementsystem.common.dto.UserDTO;
import com.fedmag.accountmanagementsystem.common.exceptions.BadRequestException;
import com.fedmag.accountmanagementsystem.common.exceptions.NotFoundException;
import com.fedmag.accountmanagementsystem.common.exceptions.UnauthenticatedException;
import com.fedmag.accountmanagementsystem.common.requests.ChangePassRequest;
import com.fedmag.accountmanagementsystem.common.requests.RegistrationRequest;
import com.fedmag.accountmanagementsystem.model.AppUser;
import com.fedmag.accountmanagementsystem.model.RoleRepo;
import com.fedmag.accountmanagementsystem.model.SecurityEventsEnum;
import com.fedmag.accountmanagementsystem.model.UserRepo;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthService {

  private final static Logger log = LoggerFactory.getLogger(AuthService.class);

  private final PasswordEncoder passwordEncoder;

  private final UserRepo userRepo;
  private final RoleRepo permissionRepository;
  private final ApplicationEventPublisher eventPublisher;
  private final HttpServletRequest request;

  private final static List<String> BREACHED_PASSWORDS = List.of("PasswordForJanuary",
      "PasswordForFebruary", "PasswordForMarch", "PasswordForApril", "PasswordForMay",
      "PasswordForJune", "PasswordForJuly", "PasswordForAugust", "PasswordForSeptember",
      "PasswordForOctober", "PasswordForNovember", "PasswordForDecember");


  private final static int MIN_PASS_LENGTH = 12;

  public AuthService(PasswordEncoder passwordEncoder, UserRepo userRepo,
      RoleRepo permissionRepository, ApplicationEventPublisher eventPublisher,
      HttpServletRequest request) {
    this.passwordEncoder = passwordEncoder;
    this.userRepo = userRepo;
    this.permissionRepository = permissionRepository;
    this.eventPublisher = eventPublisher;
    this.request = request;
  }

  public UserDTO registerUser(RegistrationRequest registrationRequest) {
    String formattedEmail = registrationRequest.email().toLowerCase();
    eventPublisher.publishEvent(
        new AppEvent(this, SecurityEventsEnum.CREATE_USER, "Anonymous", formattedEmail,
            request.getRequestURI()));

    if (!userIsValid(registrationRequest)) {
      throw new BadRequestException("Bad Request");
    }

    if (userRepo.existsByEmail(formattedEmail)) {
      log.info("User exists already");
      throw new BadRequestException("User exist!");
    }
    log.info("Creating new user..");
    AppUser user = AppUser.from(registrationRequest, passwordEncoder);
    // only the first registered user should be the admin
    if (userRepo.count() == 0) {
      log.info("First user is registering. Conceding ADMIN privileges.");
      user.addRole(permissionRepository.findByCode(RolesEnum.ADMIN.getString()).orElseThrow());
    } else {
      user.addRole(permissionRepository.findByCode(RolesEnum.USER.getString()).orElseThrow());
    }
    userRepo.save(user);
    log.info("User saved.");

    return user.toUserDTO();
  }

  public EmailStatusUpdateDTO changePass(UserDetails userDetails, ChangePassRequest passRequest) {
    if (userDetails == null) {
      throw new UnauthenticatedException("This api only for authenticated user");
    }

    eventPublisher.publishEvent(
        new AppEvent(this, SecurityEventsEnum.CHANGE_PASSWORD, userDetails.getUsername(), userDetails.getUsername(),
            request.getRequestURI()));


    log.info("User {} started change password procedure..", userDetails.getUsername());

    assert passwordIsSecure(passRequest.newPassword());

    AppUser user = userRepo.findByEmail(userDetails.getUsername()).orElseThrow(
        () -> new NotFoundException(
            "User with email " + userDetails.getUsername() + " was not found."));
    if (passwordEncoder.matches(passRequest.newPassword(), user.getPassword())) {
      throw new BadRequestException("The passwords must be different!");
    }
    log.info("All checks succeeded. Setting new password.");
    user.setPassword(passwordEncoder.encode(passRequest.newPassword()));
    userRepo.save(user);
    return new EmailStatusUpdateDTO(userDetails.getUsername(),
        "The password has been updated successfully");
  }

  private boolean userIsValid(RegistrationRequest request) {
    log.info("Checking password validity...");
    boolean nameIsValid = StringUtils.isNotBlank(request.name());
    boolean lastnameIsValid = StringUtils.isNotBlank(request.lastname());
    boolean emailIsValid =
        StringUtils.isNotBlank(request.email()) && request.email().endsWith("@acme.com");
    boolean passwordIsValidAndSecure =
        StringUtils.isNotBlank(request.password()) && passwordIsSecure(request.password());
    return nameIsValid && lastnameIsValid && emailIsValid && passwordIsValidAndSecure;
  }

  private boolean passwordIsSecure(String password) {
    log.info("Checking password security...");
    if (password.length() < MIN_PASS_LENGTH) {
      throw new BadRequestException("Password length must be 12 chars minimum!");
    }
    if (BREACHED_PASSWORDS.contains(password)) {
      throw new BadRequestException("The password is in the hacker's database!");
    }
    return true;
  }
}
