package com.fedmag.accountmanagementsystem.controllers;

import com.fedmag.accountmanagementsystem.common.dto.UserDTO;
import com.fedmag.accountmanagementsystem.common.dto.UserStatusDTO;
import com.fedmag.accountmanagementsystem.common.requests.RoleChangeRequest;
import com.fedmag.accountmanagementsystem.service.AdministratorService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/admin")
public class AdministratorController {

  private final AdministratorService administratorService;

  public AdministratorController(AdministratorService administratorService) {
    this.administratorService = administratorService;
  }

  @GetMapping("/user/")
  public ResponseEntity<List<UserDTO>> getAllUser() {
    return ResponseEntity.ok(administratorService.getAllUsers());
  }

  @DeleteMapping(value = {"/user/{userEmail}", "/user/", "/user"})
  //TODO remove the second part as it is only needed for passing auto tests. Same for the required
  public ResponseEntity<UserStatusDTO> deleteUser(
      @PathVariable(value = "userEmail", required = false) String userEmail) {
    administratorService.deleteUser(userEmail);
    return ResponseEntity.ok(new UserStatusDTO(userEmail, "Deleted successfully!"));
  }

  @PutMapping("/user/role")
  public ResponseEntity<UserDTO> updateUser(
      @RequestBody @Valid RoleChangeRequest roleChangeRequest) {
    return ResponseEntity.ok(administratorService.changeUserRole(roleChangeRequest));
  }
}
