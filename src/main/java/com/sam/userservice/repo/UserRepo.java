package com.sam.userservice.repo;

import com.sam.userservice.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<User, Long> {
    User findByUserName(String userName);
}
