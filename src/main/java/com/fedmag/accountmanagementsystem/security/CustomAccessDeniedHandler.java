package com.fedmag.accountmanagementsystem.security;


import com.fedmag.accountmanagementsystem.common.AppEvent;
import com.fedmag.accountmanagementsystem.model.SecurityEventsEnum;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

  private final ApplicationEventPublisher eventPublisher;

  @Autowired
  public CustomAccessDeniedHandler(ApplicationEventPublisher eventPublisher) {
    this.eventPublisher = eventPublisher;
  }

  @Override
  public void handle(HttpServletRequest request, HttpServletResponse response,
      AccessDeniedException accessDeniedException) throws IOException, ServletException {

    String requestingUser = request.getRemoteUser() == null ? "Anonymous" : request.getRemoteUser();
    eventPublisher.publishEvent(new AppEvent(this, SecurityEventsEnum.ACCESS_DENIED, requestingUser,
        request.getRequestURI(), request.getRequestURI()));
    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied!");
  }
}
