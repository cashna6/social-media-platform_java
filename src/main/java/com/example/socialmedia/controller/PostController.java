
package com.example.socialmedia.controller;

import com.example.socialmedia.dto.PostRequest;
import com.example.socialmedia.model.Post;
import com.example.socialmedia.service.PostService;
import com.example.socialmedia.service.UserService;
import com.example.socialmedia.model.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.LinkedHashMap;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.Map;
import java.text.ParseException;

@RestController
@RequestMapping("/post")
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<?> createPost(@RequestBody PostRequest postRequest) {
        // Manually extract fields from the request and create a Post object
        Post post = new Post();
        post.setPostBody(postRequest.getPostBody());

        // Fetch the user by ID
        Optional<User> userOptional = userService.getUserById(postRequest.getUserID());
        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(new ErrorResponse("User does not exist"));
        }
        User user = userOptional.get();
        post.setUser(user);

        // Set the creation date in the format "yyyy-MM-dd"
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String creationDateStr = dateFormat.format(new Date());

        try {
            Date creationDate = dateFormat.parse(creationDateStr);
            post.setCreationDate(creationDate);
        } catch (ParseException e) {
            // Handle parsing exception
            return ResponseEntity.badRequest().body(new ErrorResponse("Failed to parse creation date"));
        }

        // Save the post
        Post savedPost = postService.savePost(post);
        if (savedPost == null) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Failed to save post"));
        }
        return ResponseEntity.ok("Post created successfully");
    }

    @GetMapping
    @ResponseBody // This annotation ensures the return type is serialized to JSON
    public ResponseEntity<?> getPostById(@RequestParam Long postID) {
        Optional<Post> postOptional = postService.getPostById(postID);
        if (postOptional.isPresent()) {
            Post post = postOptional.get();
            Date creationDate = post.getCreationDate(); // Assuming this is a java.util.Date
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            // Convert Date to LocalDate
            String formattedDate = creationDate.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
                    .format(formatter);

            // Use LinkedHashMap to maintain insertion order
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("postID", post.getId());
            response.put("postBody", post.getPostBody());
            response.put("date", formattedDate);
            response.put("comments", post.getComments()); // Assuming getComments() returns a List

            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(new ErrorResponse("Post does not exist"));
        }
    }

    @PatchMapping
    public ResponseEntity<?> updatePost(@RequestBody UpdatePostRequest updateRequest) {
        long postId = updateRequest.getPostID();
        String postBody = updateRequest.getPostBody();

        if (!postService.doesPostExist(postId)) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Post does not exist"));
        }

        postService.updatePost(postId, postBody);
        return ResponseEntity.ok("Post edited successfully");
    }

    @DeleteMapping
    public ResponseEntity<?> deletePost(@RequestParam long postID) {
        boolean isDeleted = postService.deletePost(postID);
        if (!isDeleted) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Post does not exist"));
        }
        return ResponseEntity.ok("Post deleted");
    }

    // Inner class representing request body
    static class UpdatePostRequest {
        private long postID;
        private String postBody;

        public long getPostID() {
            return postID;
        }

        public void setPostID(long postID) {
            this.postID = postID;
        }

        public String getPostBody() {
            return postBody;
        }

        public void setPostBody(String postBody) {
            this.postBody = postBody;
        }
    }

    // Custom error response object for JSON serialization
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
