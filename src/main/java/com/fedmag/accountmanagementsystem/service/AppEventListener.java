package com.fedmag.accountmanagementsystem.service;


import com.fedmag.accountmanagementsystem.common.AppEvent;
import com.fedmag.accountmanagementsystem.model.SecurityEvent;
import com.fedmag.accountmanagementsystem.model.SecurityEventRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

@Component
public class AppEventListener {

  private final LoginService loginService;
  private final SecurityEventRepo securityEventRepo;

  @Autowired
  public AppEventListener(LoginService loginService,
      SecurityEventRepo securityEventRepo) {
    this.loginService = loginService;
    this.securityEventRepo = securityEventRepo;
  }

  @EventListener
  public void onSuccess(AuthenticationSuccessEvent successEvent) {
    loginService.handleSuccess(successEvent);
  }

  @EventListener
  public void onFailure(AbstractAuthenticationFailureEvent failureEvent) {
    loginService.handleFailure(failureEvent);
  }

  @EventListener
  public void onAppEvent(AppEvent event) {
    System.out.println(
        "\n\n>>> an event of type %s just happened. Saving into securityEventRepo <<<\n\n".formatted(
            event.getEventString()));
    securityEventRepo.save(SecurityEvent.from(event));
  }
}
