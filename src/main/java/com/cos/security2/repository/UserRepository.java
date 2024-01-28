package com.cos.security2.repository;

import com.cos.security2.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<Users, Long> {
    public Users findByUsername(String username);
}
