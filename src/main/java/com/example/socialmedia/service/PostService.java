package com.example.socialmedia.service;

import com.example.socialmedia.model.Post;
import com.example.socialmedia.repository.PostRepository;
import com.example.socialmedia.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    public Post savePost(Post post) {
        if (!userRepository.existsById(post.getUser().getId())) {
            return null;
        }
        return postRepository.save(post);
    }
    
    public boolean doesPostExist(Long postId) {
        return postRepository.existsById(postId);
    }
    
    public Optional<Post> getPostById(Long id) {
        return postRepository.findById(id);
    }

    public Optional<Post> updatePost(Long id, String postBody) {
        Optional<Post> post = postRepository.findById(id);
        post.ifPresent(p -> {
            p.setPostBody(postBody);
            postRepository.save(p);
        });
        return post;
    }

    public List<Post> getAllPostsSortedByCreationDesc() {
        return postRepository.findAll(Sort.by(Sort.Direction.DESC, "creationDate"));
    }

    public boolean deletePost(Long id) {
        if (!postRepository.existsById(id)) {
            return false;
        }
        postRepository.deleteById(id);
        return true;
    }
}