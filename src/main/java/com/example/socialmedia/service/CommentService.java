package com.example.socialmedia.service;

import com.example.socialmedia.model.Comment;
import com.example.socialmedia.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    public Comment saveComment(Comment comment) {
        return commentRepository.save(comment);
    }

    public Optional<Comment> getCommentById(Long id) {
        return commentRepository.findById(id);
    }

    // public Optional<CommentResponse> getCommentResponseById(Long id) {
    //     Optional<Comment> commentOptional = commentRepository.findById(id);
    //     return commentOptional.map(comment -> {
    //         User user = comment.getUser();
    //         UserSummary userSummary = new UserSummary(user.getId(), user.getName()); // Assumes User has getName()
    //                                                                                  // method.
    //         return new CommentResponse(comment.getId(), comment.getContent(), userSummary);
    //     });
    // }

    public List<Comment> getAllComments() {
        return commentRepository.findAll();
    }

    public void deleteComment(Long id) {
        commentRepository.deleteById(id);
    }

    public boolean doesCommentExist(long commentId) {
        ;
        return commentRepository.existsById(commentId);
    }

    public void updateComment(long commentId, String commentBody) {
        Optional<Comment> commentOptional = commentRepository.findById((long) commentId);
        commentOptional.ifPresent(comment -> {
            comment.setContent(commentBody);
            commentRepository.save(comment);
        });
    }
}
