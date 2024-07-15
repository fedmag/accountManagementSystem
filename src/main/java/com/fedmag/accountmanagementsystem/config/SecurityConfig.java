package com.fedmag.accountmanagementsystem.config;


import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  private final static Logger LOG = LoggerFactory.getLogger(SecurityConfig.class);

  @Bean
  public SecurityFilterChain mySecurityFilterChain(HttpSecurity http) throws Exception {
    http
        .httpBasic(Customizer.withDefaults())
        .csrf().disable() // For Postman
        .headers(headers -> headers.frameOptions().disable()) // For the H2 console
        .authorizeHttpRequests(auth -> auth // manage access
            .requestMatchers(HttpMethod.POST, "/api/auth/signup").permitAll()
            .requestMatchers(HttpMethod.POST, "/api/auth/changepass").authenticated()

            // employee endpoints
            .requestMatchers(HttpMethod.GET, "/api/empl/payment").hasAnyRole("USER", "ACCOUNTANT")
            .requestMatchers(HttpMethod.POST, "/api/acct/payments").hasRole("ACCOUNTANT")
            .requestMatchers(HttpMethod.PUT, "/api/acct/payments").hasRole("ACCOUNTANT")

            // admin endpoints
            .requestMatchers( "/api/admin/**").hasRole("ADMINISTRATOR")
//            .requestMatchers(HttpMethod.GET, "/api/admin/user/").hasRole("ADMINISTRATOR")
//            .requestMatchers(HttpMethod.DELETE, "/api/admin/user/{userEmail}", "/api/admin/user/", "/api/admin/user").hasRole("ADMINISTRATOR")
//            .requestMatchers(HttpMethod.PUT, "/api/admin/user/role").hasRole("ADMINISTRATOR")

            .anyRequest().permitAll()
        )
        .exceptionHandling()
        .accessDeniedHandler(
            (req, resp, ex) -> resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied!"))
        .and()
        .sessionManagement(sessions -> sessions
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // no session
        );
    return http.build();
  }

  @Bean
  PasswordEncoder getPasswordEncoder() {
    return new BCryptPasswordEncoder(15);
  }
}
