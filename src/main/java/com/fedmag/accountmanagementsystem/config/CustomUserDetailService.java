package com.fedmag.accountmanagementsystem.config;


import com.fedmag.accountmanagementsystem.model.AppUser;
import com.fedmag.accountmanagementsystem.model.Role;
import com.fedmag.accountmanagementsystem.model.UserRepo;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomUserDetailService implements UserDetailsService {

  private final UserRepo userRepo;

  public CustomUserDetailService(UserRepo userRepo) {
    this.userRepo = userRepo;
  }

  // if we want to avoid EAGER loading we need to get the lazy elements within one trx
  @Transactional
  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    AppUser appUserEntity = userRepo.findByEmail(username.toLowerCase())
        .orElseThrow(() -> new UsernameNotFoundException("User" + username + "could not be found"));
    return new User(appUserEntity.getEmail(),
        appUserEntity.getPassword(),
        getAuthorities(
            appUserEntity.getRoles())); // after the log in I am forcing every user to have the user role, this must be done during registration
  }

  private Collection<GrantedAuthority> getAuthorities(Set<Role> roles) {
    Collection<GrantedAuthority> authorities = new ArrayList<>(roles.size());
    for (Role userGroup : roles) {
      authorities.add(new SimpleGrantedAuthority(userGroup.getCode().toUpperCase()));
    }
    return authorities;
  }
}
