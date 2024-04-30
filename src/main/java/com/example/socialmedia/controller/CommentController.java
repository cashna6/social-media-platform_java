package com.example.socialmedia.controller;

import com.example.socialmedia.model.Comment;
import com.example.socialmedia.service.CommentService;
import com.example.socialmedia.service.PostService;
import com.example.socialmedia.service.UserService;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@RestController
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<?> createComment(@RequestBody CreateCommentRequest request) {

        // Check if the associated user exists
        if (!userService.doesUserExist(request.getUserID())) {
            return ResponseEntity.badRequest().body(new ErrorResponse("User does not exist"));
        }

        // Check if the associated post exists
        if (!postService.doesPostExist(request.getPostID())) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Post does not exist"));
        }

        // Create the comment
        Comment comment = new Comment();
        comment.setContent(request.getCommentBody());
        comment.setPost(postService.getPostById(request.getPostID()).orElse(null));
        comment.setUser(userService.getUserById(request.getUserID()).orElse(null));
        commentService.saveComment(comment);
        return ResponseEntity.ok("Comment created successfully");
    }

    @GetMapping
    public ResponseEntity<?> getCommentById(@RequestParam long commentID) {
        Optional<Comment> commentOptional = commentService.getCommentById(commentID);
        if (commentOptional.isPresent()) {
            return ResponseEntity.ok(commentOptional.get());
        } else {
            return ResponseEntity.badRequest().body(new ErrorResponse("Comment does not exist"));
        }
    }

    @PatchMapping
    public ResponseEntity<?> updateComment(@RequestBody UpdateCommentRequest request) {
        long commentId = request.getCommentID();
        String commentBody = request.getCommentBody();

        if (!commentService.doesCommentExist(commentId)) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Comment does not exist"));
        }

        commentService.updateComment(commentId, commentBody);
        return ResponseEntity.ok("Comment edited successfully");
    }

    @DeleteMapping
    public ResponseEntity<?> deleteComment(@RequestParam long commentID) {
        if (!commentService.doesCommentExist(commentID)) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Comment does not exist"));
        }

        commentService.deleteComment(commentID);
        return ResponseEntity.ok("Comment deleted");
    }

    // Inner classes representing request bodies
    static class CreateCommentRequest {
        private String commentBody;
        private long postID;
        private long userID;

        public String getCommentBody() {
            return commentBody;
        }

        public void setCommentBody(String commentBody) {
            this.commentBody = commentBody;
        }

        public long getPostID() {
            return postID;
        }

        public void setPostID(long postID) {
            this.postID = postID;
        }

        public long getUserID() {
            return userID;
        }

        public void setUserID(long userID) {
            this.userID = userID;
        }
    }

    static class UpdateCommentRequest {
        private String commentBody;
        private long commentID;

        public String getCommentBody() {
            return commentBody;
        }

        public void setCommentBody(String commentBody) {
            this.commentBody = commentBody;
        }

        public long getCommentID() {
            return commentID;
        }

        public void setCommentID(long commentID) {
            this.commentID = commentID;
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
