package com.sam.userservice.service;

import com.sam.userservice.domain.Role;
import com.sam.userservice.domain.User;

import java.util.List;

public interface UserService {
    User saveUser(User user);

    Role saveRole(Role role);

    void addRoleToUser(String userName, String roleName);

    User getUser(String userName);

    List<User> getUsers();
}
