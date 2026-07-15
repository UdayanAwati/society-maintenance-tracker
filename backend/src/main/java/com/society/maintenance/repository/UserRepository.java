package com.society.maintenance.repository;

import com.society.maintenance.entity.Role;
import com.society.maintenance.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByPasswordResetToken(String token);
    boolean existsByEmail(String email);
    List<User> findByRole(Role role);
}
