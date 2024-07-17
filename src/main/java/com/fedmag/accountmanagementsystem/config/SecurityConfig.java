package com.fedmag.accountmanagementsystem.config;



import com.fedmag.accountmanagementsystem.security.CustomAccessDeniedHandler;
import com.fedmag.accountmanagementsystem.security.CustomBasicAuthEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  private final static int ENCODER_STRENGTH = 15;

  private final CustomBasicAuthEntryPoint entryPoint;
  private final CustomAccessDeniedHandler customAccessDeniedHandler;

  @Autowired
  public SecurityConfig(CustomBasicAuthEntryPoint entryPoint,
      CustomAccessDeniedHandler customAccessDeniedHandler) {
    this.entryPoint = entryPoint;
    this.customAccessDeniedHandler = customAccessDeniedHandler;
  }

  @Bean
  public SecurityFilterChain mySecurityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf().disable() // For Postman
        .headers(headers -> headers.frameOptions().disable()) // For the H2 console
        .authorizeHttpRequests(auth -> auth // manage access
            .requestMatchers(HttpMethod.POST, "/api/auth/signup").permitAll()
            .requestMatchers(HttpMethod.POST, "/api/auth/changepass").authenticated()
            // employee endpoints
            .requestMatchers(HttpMethod.GET, "/api/empl/payment").hasAnyRole("USER", "ACCOUNTANT")
            .requestMatchers(HttpMethod.POST, "/api/acct/payments").hasRole("ACCOUNTANT")
            .requestMatchers(HttpMethod.PUT, "/api/acct/payments").hasRole("ACCOUNTANT")
            // auditor endpoints
            .requestMatchers("/api/security/**").hasRole("AUDITOR")
            // admin endpoints
            .requestMatchers("/api/admin/**").hasRole("ADMINISTRATOR")
            // deny all other requests
            .anyRequest().denyAll())
        .exceptionHandling()
        // handlers
        .and()
        .exceptionHandling()
        .accessDeniedHandler(customAccessDeniedHandler) // could also create the instance here

        // sets a global AuthenticationEntryPoint for ALL auth failures
//        .authenticationEntryPoint(authEntryPoint())
        .and()
        // no session
        .sessionManagement(
            sessions -> sessions.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        )
        .httpBasic(
            // sets an AuthenticationEntryPoint specifically for HTTPBasic auth failures
            conf -> conf.authenticationEntryPoint(entryPoint)
        );
    return http.build();
  }

  @Bean
  PasswordEncoder getPasswordEncoder() {
    return new BCryptPasswordEncoder(ENCODER_STRENGTH);
  }
}
