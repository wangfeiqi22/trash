package com.renewable.ai.repository;

import com.renewable.ai.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByPhone(String phone);
    List<User> findByStatus(Integer status);
    Optional<User> findByUsernameAndStatus(String username, Integer status);
}
