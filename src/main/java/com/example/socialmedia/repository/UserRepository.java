package com.example.socialmedia.repository;

import com.example.socialmedia.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // You can define custom queries here if needed
    Optional<User> findByEmail(String email);
}
