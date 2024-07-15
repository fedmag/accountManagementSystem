package com.fedmag.accountmanagementsystem.model;

import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface UserRepo extends CrudRepository<AppUser, Long> {
  Optional<AppUser> findByEmail(String email);
  boolean existsByEmail(String email);
  long deleteByEmail(String email);
}
