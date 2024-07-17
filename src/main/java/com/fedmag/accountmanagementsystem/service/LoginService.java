package com.fedmag.accountmanagementsystem.service;


import com.fedmag.accountmanagementsystem.common.AppEvent;
import com.fedmag.accountmanagementsystem.common.RolesEnum;
import com.fedmag.accountmanagementsystem.model.AppUser;
import com.fedmag.accountmanagementsystem.model.SecurityEventsEnum;
import com.fedmag.accountmanagementsystem.model.UserRepo;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class LoginService {

  public static final int MAX_ATTEMPTS = 5;

  private final UserRepo userRepo;
  private final ApplicationEventPublisher eventPublisher;
  private final HttpServletRequest request;

  public LoginService(UserRepo userRepo,
      ApplicationEventPublisher eventPublisher,
      HttpServletRequest request) {
    this.userRepo = userRepo;
    this.eventPublisher = eventPublisher;
    this.request = request;
  }

  public void handleSuccess(AuthenticationSuccessEvent successEvent) {
    String username = successEvent.getAuthentication().getName();
    AppUser user = userRepo.findByEmail(username).orElseThrow();
    if (user.getFailedAttempt() != 0) {
      user.setFailedAttempt(0);
      userRepo.save(user);
    }
  }

  public void handleFailure(AbstractAuthenticationFailureEvent failureEvent) {
    log.info("Exception: " + failureEvent.getException());
    if (failureEvent instanceof AuthenticationFailureBadCredentialsEvent) {
      log.info("Credentials provided by the user are not correct.");
      registerLoginAttempt(failureEvent);
    }
  }

  private void registerLoginAttempt(AbstractAuthenticationFailureEvent failureEvent) {
    String username = failureEvent.getAuthentication().getName();
    log.info("Registering login attempt from " + username);
    Optional<AppUser> optUser = userRepo.findByEmail(username);

    AppEvent failedLoginEvt = new AppEvent(this);
    failedLoginEvt.setDate(LocalDateTime.now());
    failedLoginEvt.setSubject(username);
    failedLoginEvt.setObject(request.getRequestURI());
    failedLoginEvt.setPath(request.getRequestURI());
    failedLoginEvt.setEventString(SecurityEventsEnum.LOGIN_FAILED.toString());
    eventPublisher.publishEvent(failedLoginEvt);
    // unknown user
    if (optUser.isEmpty()) {
      log.info("Emitting event for unknown user.");
      return;
    }

    // known user
    // we might need to send both login failed and brute force:
    AppUser user = optUser.get();
    user.setFailedAttempt(user.getFailedAttempt() + 1);

    handleBruteForce(user);
    handleUserLock(user); // here we have side-effects, not a big fan
    userRepo.save(user);
  }

  private void handleBruteForce(AppUser user) {
    if (user.getFailedAttempt() == MAX_ATTEMPTS) {
      AppEvent bruteForceEvt = new AppEvent(this,
          SecurityEventsEnum.BRUTE_FORCE,
          user.getEmail(), request.getRequestURI(), request.getRequestURI()
      );
      log.info("Emitting brute force event for user " + user.getEmail());
      eventPublisher.publishEvent(bruteForceEvt);
    }
  }

  private void handleUserLock(AppUser user) {
    if (accountShouldBeLocked(user)) {
      user.setAccountLocked(true);
      // only send the event for the 5th attempt, not subsequent
      if (user.getFailedAttempt() == MAX_ATTEMPTS) {
        log.info("User has: %s failed  attempt now.".formatted(user.getFailedAttempt()));
        AppEvent lockUserEvent = new AppEvent(this, SecurityEventsEnum.LOCK_USER, user.getEmail(),
            "Lock user %s".formatted(user.getEmail()), request.getRequestURI());
        log.info("Emitting lock event for user " + user.getEmail());
        eventPublisher.publishEvent(lockUserEvent);
      }
    }
  }

  private boolean accountShouldBeLocked(AppUser user) {
    if (user.getFailedAttempt() < MAX_ATTEMPTS) {
      return false;
    }
    if (user.hasRole(RolesEnum.ADMIN)) {
      log.info("Cannot block ADMIN USER");
      return false;
    }
    return true;
  }
}
