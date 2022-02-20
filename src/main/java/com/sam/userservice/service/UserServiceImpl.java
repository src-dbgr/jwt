package com.sam.userservice.service;

import com.sam.userservice.domain.Role;
import com.sam.userservice.domain.User;
import com.sam.userservice.repo.RoleRepo;
import com.sam.userservice.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserServiceImpl implements UserService, UserDetailsService {

  private final UserRepo userRepo;
  private final RoleRepo roleRepo;
  private final PasswordEncoder passwordEncoder;

  @Override
  public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
    User user = userRepo.findByUserName(userName);
    if (user == null) {
      log.error("User not found in the database");
      throw new UsernameNotFoundException("User not found in the database");
    } else {
      log.error("User {} found in the database", userName);
    }
    Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
    user.getRoles()
        .forEach(
            role -> {
              authorities.add(new SimpleGrantedAuthority(role.getName()));
            });
    return new org.springframework.security.core.userdetails.User(
        user.getUserName(), user.getPassword(), authorities);
  }

  @Override
  public User saveUser(User user) {
    log.info("Saving new user {} to the database", user.getName());
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    return userRepo.save(user);
  }

  @Override
  public Role saveRole(Role role) {
    log.info("Saving new role {} to the database", role.getName());
    return roleRepo.save(role);
  }

  @Override
  public void addRoleToUser(String userName, String roleName) {
    log.info("Adding role {} to user {}", roleName, userName);
    User user = userRepo.findByUserName(userName);
    Role role = roleRepo.findByName(roleName);
    user.getRoles().add(role);
  }

  @Override
  public User getUser(String userName) {
    log.info("Fetching user {}", userName);
    return userRepo.findByUserName(userName);
  }

  @Override
  public List<User> getUsers() {
    log.info("Fetching all users");
    return userRepo.findAll();
  }
}
