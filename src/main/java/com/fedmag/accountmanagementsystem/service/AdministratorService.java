package com.fedmag.accountmanagementsystem.service;


import com.fedmag.accountmanagementsystem.common.OperationsEnum;
import com.fedmag.accountmanagementsystem.common.RolesEnum;
import com.fedmag.accountmanagementsystem.common.dto.UserDTO;
import com.fedmag.accountmanagementsystem.common.exceptions.BadRequestException;
import com.fedmag.accountmanagementsystem.common.exceptions.NotFoundException;
import com.fedmag.accountmanagementsystem.common.requests.RoleChangeRequest;
import com.fedmag.accountmanagementsystem.model.AppUser;
import com.fedmag.accountmanagementsystem.model.RoleRepo;
import com.fedmag.accountmanagementsystem.model.UserRepo;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdministratorService {

  private final UserRepo userRepo;
  private final RoleRepo roleRepo;

  private final static Set<RolesEnum> ADMINISTRATIVE_ROLES = Set.of(RolesEnum.ADMIN);
  private final static Set<RolesEnum> BUSINESS_ROLES = Set.of(RolesEnum.ACCOUNTANT, RolesEnum.USER);

  @Autowired
  public AdministratorService(UserRepo userRepo, RoleRepo roleRepo) {
    this.userRepo = userRepo;
    this.roleRepo = roleRepo;
  }

  public List<UserDTO> getAllUsers() {
    List<UserDTO> users = new ArrayList<>();
    Iterable<AppUser> allUsersEntity = userRepo.findAll();
    for (AppUser userEntity : allUsersEntity) {
      users.add(userEntity.toUserDTO());
    }
    Collections.sort(users, Comparator.comparing(UserDTO::getId));
    return users;
  }

  @Transactional
  public void deleteUser(String userEmail) {
    userEmail = userEmail.toLowerCase();
    Optional<AppUser> deletingUser = userRepo.findByEmail(userEmail);
    if (deletingUser.isEmpty()) {
      throw new NotFoundException("User not found!");
    }
    AppUser user = deletingUser.get();
    if (userHasRole(user, RolesEnum.ADMIN)) {
      throw new BadRequestException("Can't remove ADMINISTRATOR role!");
    }
    userRepo.deleteByEmail(userEmail);
  }

  public UserDTO changeUserRole(RoleChangeRequest changeRequest) {
    OperationsEnum operation = OperationsEnum.valueOf(changeRequest.operation().toUpperCase());
    String roleToModifyStr = "ROLE_" + changeRequest.role(); // TODO we can modify the Enum to respond to both cases, ex: having another string
    RolesEnum roleToModify = RolesEnum.findByStringOrThrow(roleToModifyStr,
        new NotFoundException("Role not found!"));
    Optional<AppUser> userOpt = userRepo.findByEmail(changeRequest.user().toLowerCase());

    if (userOpt.isEmpty()) {
      throw new NotFoundException("User not found!");
    }

    AppUser user = userOpt.get();
    switch (operation) {
      case GRANT -> addUserRole(user, roleToModify);
      case REMOVE -> removeUserRole(user, roleToModify);
    }

    userRepo.save(user);
    return user.toUserDTO();
  }

  private void addUserRole(AppUser user, RolesEnum roleToAdd) {
    List<RolesEnum> userRoles = user.getRoles()
        .stream()
        .map(r -> RolesEnum.findByStringOrThrow(r.getCode(),
            new NotFoundException("Role Not Found")))
        .toList();
    if (!rolesAreCompatible(userRoles, roleToAdd)) {
      throw new BadRequestException("The user cannot combine administrative and business roles!");
    }
    user.addRole(
        roleRepo.findByCode(roleToAdd.getString())
            .orElseThrow(() -> new NotFoundException("Role not found!")));
  }

  private boolean rolesAreCompatible(List<RolesEnum> userRoles, RolesEnum roleToAdd) {
    boolean userIsBusiness = userRoles.stream().anyMatch(BUSINESS_ROLES::contains);
    boolean userIsAdministrative = userRoles.stream().anyMatch(ADMINISTRATIVE_ROLES::contains);
    boolean roleIsBusiness = BUSINESS_ROLES.contains(roleToAdd);
    boolean roleIsAdministrative = ADMINISTRATIVE_ROLES.contains(roleToAdd);
    return (userIsBusiness && roleIsBusiness) || (userIsAdministrative && roleIsAdministrative);
  }

  private void removeUserRole(AppUser user, RolesEnum roleToRemove) {
    if (roleToRemove == RolesEnum.ADMIN) {
      throw new BadRequestException("Can't remove ADMINISTRATOR role!");
    }
    if (!userHasRole(user, roleToRemove)) {
      throw new BadRequestException("The user does not have a role!");
    }
    if (user.getRoles().size() == 1) {
      throw new BadRequestException("The user must have at least one role!");
    }

    user.removeRole(
        roleRepo.findByCode(roleToRemove.getString())
            .orElseThrow(() -> new NotFoundException("NOT IMPLEMENTED YET")));
  }

  private boolean userHasRole(AppUser user, RolesEnum role) {
    return user.getRoles().stream()
        .anyMatch(r -> r.getCode().equals(
            role.getString())); // TODO this might be done with the find by stirng in the enum
  }
}
