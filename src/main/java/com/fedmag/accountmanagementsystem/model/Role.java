package com.fedmag.accountmanagementsystem.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.util.Set;

@Entity
@Table(name = "roles")
public class Role {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Column(name = "code", unique = true)
  private String code;

  private String name;

  @JsonIgnore
  @ManyToMany(mappedBy = "roles")
  private Set<AppUser> users;

  public Role() {
  }

  public Role(String code, String name) {
    this.code = code;
    this.name = name;
  }

  public Role(long id, String code, String name, Set<AppUser> users) {
    this(code, name);
    this.id = id;
    this.users = users;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Set<AppUser> getUsers() {
    return users;
  }

  public void setUsers(Set<AppUser> users) {
    this.users = users;
  }
}
