package com.example.socialmedia.repository;

import com.example.socialmedia.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    // Additional methods to find posts by user or status can be added here
}
