package com.shopzy.ecom.repository;

import com.shopzy.ecom.entity.User;
import com.shopzy.ecom.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findFirstByEmail(String email);
    User findByRole(UserRole userRole);
}
