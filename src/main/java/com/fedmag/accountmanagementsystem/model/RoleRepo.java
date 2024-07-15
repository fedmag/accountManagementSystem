package com.fedmag.accountmanagementsystem.model;

import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface RoleRepo extends CrudRepository<Role, Long> {
  Optional<Role> findByCode(String code);
  boolean existsByCode(String code);
}
