package com.fedmag.accountmanagementsystem.common;

import java.util.Arrays;

public enum OperationsEnum {
  GRANT,
  REMOVE;

  public static RolesEnum findByString(String groupName) {
    return Arrays.stream(RolesEnum.values())
        .filter(group -> group.getString().equalsIgnoreCase(groupName)).findFirst().orElseThrow();
  }
}
