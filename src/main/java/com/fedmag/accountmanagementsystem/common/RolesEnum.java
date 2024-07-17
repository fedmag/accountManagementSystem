package com.fedmag.accountmanagementsystem.common;

import java.util.Arrays;

public enum RolesEnum {
  ADMIN("ROLE_ADMINISTRATOR"),
  USER("ROLE_USER"),
  ACCOUNTANT("ROLE_ACCOUNTANT"),
  AUDITOR("ROLE_AUDITOR");

  private String string;

  RolesEnum(String string) {
    this.string = string;
  }

  public String getString() {
    return string;
  }

  public static RolesEnum findByStringOrThrow(String role, RuntimeException ex) {
    return Arrays.stream(RolesEnum.values())
        .filter(r -> r.getString().equalsIgnoreCase(role)).findFirst().orElseThrow(() -> ex);
  }
}
