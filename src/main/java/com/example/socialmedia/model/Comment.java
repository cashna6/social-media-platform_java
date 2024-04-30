package com.example.socialmedia.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;

@Entity
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("commentID")
    private Long id;

    @Column(nullable = false)
    @JsonProperty("commentBody")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    @JsonIgnore
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    // Constructors, getters, and setters
    public Comment() {}

    // Getters with JSON Properties for User details
    @JsonProperty("commentCreator")
    public UserSummary getCommentCreator() {
        return new UserSummary(this.user);
    }

    public static class UserSummary {
        @JsonProperty("userID")
        private Long userID;

        @JsonProperty("name")
        private String name;

        public UserSummary(User user) {
            this.userID = user.getId();
            this.name = user.getName();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
