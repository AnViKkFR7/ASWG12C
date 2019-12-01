package com.aswg12c.demo.controller;

import com.aswg12c.demo.exceptions.ExceptionMessages;
import com.aswg12c.demo.exceptions.GenericException;
import com.aswg12c.demo.model.Comment;
import com.aswg12c.demo.model.CommentDTO;
import com.aswg12c.demo.model.Issue;
import com.aswg12c.demo.model.Session;
import com.aswg12c.demo.model.User;
import com.aswg12c.demo.repository.CommentRepository;
import com.aswg12c.demo.repository.IssueRepository;
import com.aswg12c.demo.repository.SessionRepository;
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
import org.springframework.web.bind.annotation.RequestParam;
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

  @Autowired
  private SessionRepository sessionRepository;

  @GetMapping("{id}") //coger un issue con un id concreto
  Comment getComment(@PathVariable Long id){
    return commentRepository.findById(id).orElseThrow(
        () -> new GenericException(HttpStatus.NOT_FOUND,
            ExceptionMessages.COMMENT_ID_NOT_FOUND.getErrorMessage()));
  }

  @PostMapping("/newComment")
  @ResponseStatus(HttpStatus.CREATED)
  Comment newCommentByParams(@RequestBody CommentDTO newCommentDTO, @RequestParam(name = "token") String facebook_token){
    Session actual_session = sessionRepository.findByToken(facebook_token);
    if (actual_session == null) throw new GenericException(HttpStatus.FORBIDDEN, "There is no session with that token");
    if (!actual_session.getLoggedIn()) throw new GenericException(HttpStatus.FORBIDDEN, "You are not logged in");

    User creator = userRepository.findByFacebookId(actual_session.getUserId());

    if (creator == null) {
      throw new GenericException(HttpStatus.NOT_FOUND, ExceptionMessages.COMMENT_ID_NOT_FOUND.getErrorMessage());
    }

    Issue issueCommented = issueRepository.findById(newCommentDTO.getIssueId()).orElseThrow(
        () -> new GenericException(HttpStatus.NOT_FOUND,
            ExceptionMessages.ID_NOT_FOUND.getErrorMessage()));

    Comment newComment = new Comment(newCommentDTO.getContent(), creator, issueCommented, true);
    commentRepository.save(newComment);
    return newComment;
  }

  @DeleteMapping("{id}")
  @ResponseStatus(HttpStatus.OK)
  void deleteComment(@PathVariable Long id, @RequestParam(name = "token") String facebook_token){

    Comment actual_comment = commentRepository.findById(id).orElseThrow(
        () -> new GenericException(HttpStatus.NOT_FOUND,
            ExceptionMessages.COMMENT_ID_NOT_FOUND.getErrorMessage()));
    if (actual_comment.getIssueCommented().getCreator().getFacebookToken().equals(facebook_token)) {
      commentRepository.deleteById(id);
    }
    else throw new GenericException(HttpStatus.FORBIDDEN, "You can not delete a comment that is not yours");
  }

  @PutMapping("{id}")
  @ResponseStatus(HttpStatus.OK)
  Comment editComment(@PathVariable Long id, @RequestBody Comment commentEdited){
    if (commentEdited.getContent().isEmpty()){
      throw new GenericException(HttpStatus.BAD_REQUEST,
          ExceptionMessages.CONTENT_NOT_FOUND.getErrorMessage());
    }
    //comprobar que el comentario existe
    Comment old_comment = commentRepository.findById(id).orElseThrow(
        () -> new GenericException(HttpStatus.NOT_FOUND,
            ExceptionMessages.COMMENT_ID_NOT_FOUND.getErrorMessage()));
    //comprobar que no edite el comentario otro usuario
    if (old_comment.getCreator() != commentEdited.getCreator()) {
      throw new GenericException(HttpStatus.FORBIDDEN,
          ExceptionMessages.DIFFERENT_CREATOR.getErrorMessage());
    }
    commentRepository.save(commentEdited);
    return commentEdited;
  }
}
