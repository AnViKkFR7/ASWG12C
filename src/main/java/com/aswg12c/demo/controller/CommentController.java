package com.aswg12c.demo.controller;

import com.aswg12c.demo.exceptions.ExceptionMessages;
import com.aswg12c.demo.exceptions.GenericException;
import com.aswg12c.demo.model.Comment;
import com.aswg12c.demo.model.CommentDTO;
import com.aswg12c.demo.model.Issue;
import com.aswg12c.demo.model.User;
import com.aswg12c.demo.repository.CommentRepository;
import com.aswg12c.demo.repository.IssueRepository;
import com.aswg12c.demo.repository.UserRepository;
import java.util.Date;
import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api" + CommentController.PATH)
public class CommentController {
  public static final String PATH = "/comments";

  @Autowired
  private CommentRepository commentRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private IssueRepository issueRepository;

  @GetMapping("{id}") //coger un issue con un id concreto
  Comment getComment(@PathVariable Long id){
    return commentRepository.findById(id).orElseThrow(
        () -> new GenericException(HttpStatus.BAD_REQUEST,
            ExceptionMessages.ID_NOT_FOUND.getErrorMessage()));
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  Comment newComment(@Valid @RequestBody Comment createNewComment){
    if (createNewComment.getContent().isEmpty()){
      throw new GenericException(HttpStatus.FORBIDDEN,
          ExceptionMessages.CONTENT_NOT_FOUND.getErrorMessage());
    }
    commentRepository.save(createNewComment);
    return createNewComment;
  }

  @PostMapping("/new")
  @ResponseStatus(HttpStatus.CREATED)
  Comment newCommentByParams(@RequestBody CommentDTO newCommentDTO){
    User creator = userRepository.findById(newCommentDTO.getUserId()).orElseThrow(
        () -> new GenericException(HttpStatus.BAD_REQUEST,
            ExceptionMessages.ID_NOT_FOUND.getErrorMessage()));
    Issue issueCommented = issueRepository.findById(newCommentDTO.getIssueId()).orElseThrow(
        () -> new GenericException(HttpStatus.BAD_REQUEST,
            ExceptionMessages.ID_NOT_FOUND.getErrorMessage()));
    Date today = new Date();
    Comment newComment = new Comment(newCommentDTO.getContent(), creator, issueCommented, today);
    commentRepository.save(newComment);
    return newComment;
  }

  @DeleteMapping("{id}")
  @ResponseStatus(HttpStatus.OK)
  void deleteComment(@PathVariable Long id){
    commentRepository.deleteById(id);
  }

  @PutMapping("{id}")
  @ResponseStatus(HttpStatus.OK)
  Comment editComment(@PathVariable Long id, @RequestBody Comment commentEdited){
    if (commentEdited.getContent().isEmpty()){
      throw new GenericException(HttpStatus.FORBIDDEN,
          ExceptionMessages.CONTENT_NOT_FOUND.getErrorMessage());
    }
    //comprobar que el comentario existe
    Comment old_comment = commentRepository.findById(id).orElseThrow(
        () -> new GenericException(HttpStatus.FORBIDDEN,
            ExceptionMessages.ID_NOT_FOUND.getErrorMessage()));
    //comprobar que no edite el comentario otro usuario
    if (old_comment.getCreator() != commentEdited.getCreator()) {
      throw new GenericException(HttpStatus.BAD_REQUEST,
          ExceptionMessages.DIFFERENT_CREATOR.getErrorMessage());
    }
    commentRepository.deleteById(id);
    commentRepository.save(commentEdited);
    return commentEdited;
  }
}
