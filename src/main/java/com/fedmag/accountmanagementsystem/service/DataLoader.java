package com.fedmag.accountmanagementsystem.service;


import com.fedmag.accountmanagementsystem.common.RolesEnum;
import com.fedmag.accountmanagementsystem.model.Role;
import com.fedmag.accountmanagementsystem.model.RoleRepo;
import jakarta.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DataLoader {

  private final RoleRepo roleRepo;

  @Autowired
  public DataLoader(RoleRepo roleRepo) {
    this.roleRepo = roleRepo;
  }

  @PostConstruct
  private void initializePermission() {
    log.info(">>>> Initializing permission table...");
    Set<Role> roles = Arrays.stream(RolesEnum.values())
        .map(group -> new Role(group.getString(), group.getString()))
        .collect(Collectors.toSet());

    // Only save permission groups that do not already exist
    roles.forEach(permissionGroup -> {
      if (!roleRepo.existsByCode(permissionGroup.getCode())) {
        roleRepo.save(permissionGroup);
      }
    });
  }
}
