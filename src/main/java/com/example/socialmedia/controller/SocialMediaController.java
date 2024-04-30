package com.example.socialmedia.controller;

import com.example.socialmedia.model.User;
import com.example.socialmedia.model.Post;
import com.example.socialmedia.service.UserService;
import com.example.socialmedia.service.PostService;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class SocialMediaController {

    @Autowired
    private UserService userService;

    @Autowired
    private PostService postService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody User user) {
        if (userService.emailExists(user.getEmail())) {
            return ResponseEntity.status(403).body(new ErrorResponse("Forbidden, Account already exists"));
        } else {
            userService.saveUser(user);
            return ResponseEntity.ok("Account Creation Successful");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        if (!userService.emailExists(user.getEmail())) {
            return ResponseEntity.status(404).body(new ErrorResponse("User does not exist"));
        } else {
            boolean authenticated = userService.authenticateUser(user.getEmail(), user.getPassword());
            if (authenticated) {
                return ResponseEntity.ok("Login Successful");
            } else {
                return ResponseEntity.badRequest().body(new ErrorResponse("Username/Password Incorrect"));
            }
        }
    }

    @GetMapping("/users")
    public ResponseEntity<List<Map<String, Object>>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        List<Map<String, Object>> simplifiedUsers = users.stream().map(user -> {
            Map<String, Object> userMap = new LinkedHashMap<>();
            userMap.put("userID", user.getId());
            userMap.put("name", user.getName());
            userMap.put("email", user.getEmail());
            return userMap;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(simplifiedUsers);
    }

    @GetMapping("/user")
    public ResponseEntity<?> getUserDetails(@RequestParam("userID") Long id) {
        Optional<User> userOptional = userService.getUserById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            Map<String, Object> simplifiedUser = new LinkedHashMap<>();
            simplifiedUser.put("userID", user.getId());
            simplifiedUser.put("name", user.getName());
            simplifiedUser.put("email", user.getEmail());
            return ResponseEntity.ok(simplifiedUser);
        } else {
            return ResponseEntity.status(404).body(new ErrorResponse("User does not exist"));
        }
    }
    

    @GetMapping("/")
    public ResponseEntity<List<Map<String, Object>>> getAllPosts() {
        List<Post> posts = postService.getAllPostsSortedByCreationDesc();
        List<Map<String, Object>> response = new ArrayList<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (Post post : posts) {
            Map<String, Object> postMap = new LinkedHashMap<>();
            postMap.put("postID", post.getId());
            postMap.put("postBody", post.getPostBody());
            postMap.put("date", post.getCreationDate().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
                    .format(formatter));
            postMap.put("comments", post.getComments()); // Assume getComments() returns the formatted list of comments

            response.add(postMap);
        }

        return ResponseEntity.ok(response);
    }

    static class ErrorResponse {
        @JsonProperty("Error")
        private String error;

        public ErrorResponse(String error) {
            this.error = error;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }
    }

}
