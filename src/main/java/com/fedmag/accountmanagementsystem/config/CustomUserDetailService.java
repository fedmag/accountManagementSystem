package com.fedmag.accountmanagementsystem.config;


import com.fedmag.accountmanagementsystem.model.AppUser;
import com.fedmag.accountmanagementsystem.model.Role;
import com.fedmag.accountmanagementsystem.model.UserRepo;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailService implements UserDetailsService {

  private final UserRepo userRepo;

  public CustomUserDetailService(UserRepo userRepo) {
    this.userRepo = userRepo;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    AppUser user = userRepo.findByEmail(username.toLowerCase())
        .orElseThrow(() -> new UsernameNotFoundException("User" + username + "could not be found"));
    if (user.isAccountLocked()) {
      System.out.println("User %s's account is locked.".formatted(username));
      throw new LockedException("Fuck you");
    }
    return new User(user.getEmail(),
        user.getPassword(),
        true,
        true,
        true,
        !user.isAccountLocked(),
        getAuthorities(
            user.getRoles()));
  }

  private Collection<GrantedAuthority> getAuthorities(Set<Role> roles) {
    Collection<GrantedAuthority> authorities = new ArrayList<>(roles.size());
    for (Role userGroup : roles) {
      authorities.add(new SimpleGrantedAuthority(userGroup.getCode().toUpperCase()));
    }
    return authorities;
  }
}
