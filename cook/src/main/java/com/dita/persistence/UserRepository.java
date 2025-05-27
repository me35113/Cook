package com.dita.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dita.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByUsername(String username);
}

