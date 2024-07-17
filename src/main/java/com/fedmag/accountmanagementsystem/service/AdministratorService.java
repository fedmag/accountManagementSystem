package com.fedmag.accountmanagementsystem.service;


import com.fedmag.accountmanagementsystem.common.AppEvent;
import com.fedmag.accountmanagementsystem.common.OperationsEnum;
import com.fedmag.accountmanagementsystem.common.RolesEnum;
import com.fedmag.accountmanagementsystem.common.dto.StatusUpdateDTO;
import com.fedmag.accountmanagementsystem.common.dto.UserDTO;
import com.fedmag.accountmanagementsystem.common.exceptions.BadRequestException;
import com.fedmag.accountmanagementsystem.common.exceptions.NotFoundException;
import com.fedmag.accountmanagementsystem.common.requests.ChangeAccessRequest;
import com.fedmag.accountmanagementsystem.common.requests.RoleChangeRequest;
import com.fedmag.accountmanagementsystem.model.AppUser;
import com.fedmag.accountmanagementsystem.model.RoleRepo;
import com.fedmag.accountmanagementsystem.model.SecurityEventsEnum;
import com.fedmag.accountmanagementsystem.model.UserRepo;
import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class AdministratorService {

  private final static Set<RolesEnum> ADMINISTRATIVE_ROLES = Set.of(RolesEnum.ADMIN);
  private final static Set<RolesEnum> BUSINESS_ROLES = Set.of(RolesEnum.ACCOUNTANT, RolesEnum.USER,
      RolesEnum.AUDITOR);
  private final UserRepo userRepo;
  private final RoleRepo roleRepo;
  private final ApplicationEventPublisher eventPublisher;
  private final HttpServletRequest request;

  @Autowired
  public AdministratorService(UserRepo userRepo, RoleRepo roleRepo,
      ApplicationEventPublisher eventPublisher, HttpServletRequest request) {
    this.userRepo = userRepo;
    this.roleRepo = roleRepo;
    this.eventPublisher = eventPublisher;
    this.request = request;
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

  public void deleteUser(String userEmail) {
    userEmail = userEmail.toLowerCase();
    Optional<AppUser> deletingUser = userRepo.findByEmail(userEmail);
    if (deletingUser.isEmpty()) {
      throw new NotFoundException("User not found!");
    }
    AppUser user = deletingUser.get();
    if (user.hasRole(RolesEnum.ADMIN)) {
      throw new BadRequestException("Can't remove ADMINISTRATOR role!");
    }
    eventPublisher.publishEvent(
        new AppEvent(this, SecurityEventsEnum.DELETE_USER,
            request.getRemoteUser().toLowerCase(), userEmail, request.getRequestURI()));
    userRepo.delete(user);
  }

  public StatusUpdateDTO changeUserAccess(ChangeAccessRequest accessRequest) {
    AppUser user = userRepo.findByEmail(accessRequest.user().toLowerCase()).orElseThrow();

    switch (accessRequest.operation().toUpperCase()) {
      case "LOCK" -> {
        if (user.hasRole(RolesEnum.ADMIN)) {
          throw new BadRequestException("Can't lock the ADMINISTRATOR!");
        }
        if (user.isAccountLocked()) {
          break;
        }
        //if not already locked
        user.setAccountLocked(true);

        eventPublisher.publishEvent( new AppEvent(this,
            SecurityEventsEnum.LOCK_USER,
            request.getRemoteUser(),
            "Lock user %s".formatted(user.getEmail()),
            request.getRequestURI()));
      }
      case "UNLOCK" -> {
        user.setAccountLocked(false);
        user.setFailedAttempt(0);

        eventPublisher.publishEvent(new AppEvent(this,
            SecurityEventsEnum.UNLOCK_USER,
            request.getRemoteUser(),
            "Unlock user %s".formatted(user.getEmail()),
            request.getRequestURI()));
      }
    }
    userRepo.save(user);

    String message = "User %s %sed!".formatted(user.getEmail(),
        accessRequest.operation().toLowerCase());
    return new StatusUpdateDTO(message);
  }

  public UserDTO changeUserRole(RoleChangeRequest changeRequest) {
    OperationsEnum operation = OperationsEnum.valueOf(changeRequest.operation().toUpperCase());
    String roleToModifyStr = "ROLE_"
        + changeRequest.role(); // TODO we can modify the Enum to respond to both cases, ex: having another string
    RolesEnum roleToModify = RolesEnum.findByStringOrThrow(roleToModifyStr,
        new NotFoundException("Role not found!"));
    Optional<AppUser> userOpt = userRepo.findByEmail(changeRequest.user().toLowerCase());

    if (userOpt.isEmpty()) {
      throw new NotFoundException("User not found!");
    }

    AppUser user = userOpt.get();
    AppEvent event = new AppEvent(this, null, request.getRemoteUser(), null,
        request.getRequestURI());
    switch (operation) {
      case GRANT -> {
        event.setEventString(SecurityEventsEnum.GRANT_ROLE.toString());
        event.setObject("Grant role %s to %s".formatted(changeRequest.role(), user.getEmail()));
        addUserRole(user, roleToModify);
      }
      case REMOVE -> {
        event.setEventString(SecurityEventsEnum.REMOVE_ROLE.toString());
        event.setObject("Remove role %s from %s".formatted(changeRequest.role(), user.getEmail()));
        removeUserRole(user, roleToModify);
      }
    }
    eventPublisher.publishEvent(event);

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
    if (!user.hasRole(roleToRemove)) {
      throw new BadRequestException("The user does not have a role!");
    }
    if (user.getRoles().size() == 1) {
      throw new BadRequestException("The user must have at least one role!");
    }

    user.removeRole(
        roleRepo.findByCode(roleToRemove.getString())
            .orElseThrow(() -> new NotFoundException("NOT IMPLEMENTED YET")));
  }

}
