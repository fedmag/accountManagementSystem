package com.fedmag.accountmanagementsystem.common.dto;


import com.fedmag.accountmanagementsystem.model.AppUser;
import com.fedmag.accountmanagementsystem.model.Role;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserDTO {

  private long id;
  private String name;
  private String lastname;
  private String email;
  private Set<String> roles;

  public UserDTO() {
  }

  public UserDTO(long id, String name, String lastname, String email,
      Set<Role> roles) {
    this.id = id;
    this.name = name;
    this.lastname = lastname;
    this.email = email;
    this.roles = roles.stream().map(Role::getName).collect(Collectors.toSet());
  }

  public static UserDTO from(AppUser appUser) {
    return new UserDTO(appUser.getId(), appUser.getName(),
        appUser.getLastname(), appUser.getEmail(), appUser.getRoles());
  }

}
