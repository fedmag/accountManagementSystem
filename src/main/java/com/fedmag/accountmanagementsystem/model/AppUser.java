package com.fedmag.accountmanagementsystem.model;


import com.fedmag.accountmanagementsystem.common.RolesEnum;
import com.fedmag.accountmanagementsystem.common.dto.UserDTO;
import com.fedmag.accountmanagementsystem.common.requests.RegistrationRequest;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;
import org.springframework.security.crypto.password.PasswordEncoder;

@Entity
@Table(name = "Users")
public class AppUser {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;
  private String name;
  private String lastname;
  private String email;
  private String password;
  private boolean accountLocked = false;
  private int failedAttempt = 0;

  // if we want to avoid EAGER loading we need to get the lazy elements within one trx
  @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
  @JoinTable(
      name = "user_groups",
      joinColumns = @JoinColumn(name = "user_id"),
      inverseJoinColumns = @JoinColumn(name = "permission_group_id")
  )
  private Set<Role> roles = new HashSet<>();

  public AppUser() {
  }

  public static AppUser from(RegistrationRequest registrationRequest,
      PasswordEncoder passwordEncoder) {
    AppUser usr = new AppUser();
    usr.setEmail(registrationRequest.email().toLowerCase());
    usr.setPassword(passwordEncoder.encode(registrationRequest.password()));
    usr.setLastname(registrationRequest.lastname());
    usr.setName(registrationRequest.name());
    return usr;
  }

  public UserDTO toUserDTO() {
    return new UserDTO(id, name, lastname, email, roles);
  }

  public long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getLastname() {
    return lastname;
  }

  public void setLastname(String lastname) {
    this.lastname = lastname;
  }

  public Set<Role> getRoles() {
    return roles;
  }

  public void setRoles(Set<Role> roles) {
    this.roles = roles;
  }

  public boolean isAccountLocked() {
    return accountLocked;
  }

  public void setAccountLocked(boolean accountLocked) {
    this.accountLocked = accountLocked;
  }

  public int getFailedAttempt() {
    return failedAttempt;
  }

  public void setFailedAttempt(int failedAttempt) {
    this.failedAttempt = failedAttempt;
  }

  public void addRole(Role group) {
    this.roles.add(group);
  }

  public void removeRole(Role group) {
    this.roles.remove(group);
  }

  public boolean hasRole(RolesEnum role) {
    return this.getRoles().stream()
        .anyMatch(r -> r.getCode().equals(
            role.getString())); // TODO this might be done with the find by stirng in the enum
  }
}
