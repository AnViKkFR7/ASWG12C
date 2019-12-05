package com.aswg12c.demo.controller;

import com.aswg12c.demo.exceptions.ExceptionMessages;
import com.aswg12c.demo.exceptions.GenericException;
import com.aswg12c.demo.model.Attachment;
import com.aswg12c.demo.model.Comment;
import com.aswg12c.demo.model.Issue;
import com.aswg12c.demo.model.IssueDTO;
import com.aswg12c.demo.model.Session;
import com.aswg12c.demo.model.User;
import com.aswg12c.demo.model.Vote;
import com.aswg12c.demo.model.Watches;
import com.aswg12c.demo.model.kindEnum;
import com.aswg12c.demo.model.priorityEnum;
import com.aswg12c.demo.model.sortEnum;
import com.aswg12c.demo.model.statusEnum;
import com.aswg12c.demo.repository.AttachmentRepository;
import com.aswg12c.demo.repository.CommentRepository;
import com.aswg12c.demo.repository.IssueRepository;
import com.aswg12c.demo.repository.SessionRepository;
import com.aswg12c.demo.repository.UserRepository;

import com.aswg12c.demo.repository.VotesRepository;
import com.aswg12c.demo.repository.WatchesRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api" + IssueController.PATH)
//Coses del swagger
@Api(value = "Create a new comment", description = "Issue operations")
//End of swagger
public class IssueController {
  public static final String PATH = "/issues";

  @Autowired
  private IssueRepository issueRepository;

  @Autowired
  private CommentRepository commentRepository;

  @Autowired
  private SessionRepository sessionRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private AttachmentRepository attachmentRepository;

  @Autowired
  private VotesRepository votesRepository;

  @Autowired
  private WatchesRepository watchesRepository;

  @GetMapping("/{id}") //coger un issue con un id concreto
  //Coses del swagger
  @ApiOperation(value = "Get issue by id")
  //End of swagger
  Issue getIssue(@PathVariable Long id){
    return issueRepository.findById(id).orElseThrow(
                    () -> new GenericException(HttpStatus.NOT_FOUND,
                        ExceptionMessages.ID_NOT_FOUND.getErrorMessage()));

  }

  //los select son el get de cuando le clicas en el tipo de issue en la tabla y te salen solo los de ese tipo (es acumulativo).
  //los order son de cuando le das en la tabla a ordenarlos según el tipo.
  @GetMapping()
  //Coses del swagger
  @ApiOperation(value = "Get issues by keyword and sorted by keyword")
  //End of swagger
  List<Issue> getIssuesBy(
      @RequestParam(name = "selectStatus", required = false) statusEnum selectStatus,
      @RequestParam(name = "selectPriority", required = false) priorityEnum selectPriority,
      @RequestParam(name = "selectKind", required = false) kindEnum selectKind,
      @RequestParam(name = "sortBy", required = false) sortEnum sortBy,
      @RequestParam(name = "ASC", required = false) String asc_desc) {
    List<Issue> issues = new ArrayList<>();

    //primero ordenamos
    switch (sortBy){
      case KIND:
        Collections.sort(issues, new kindComparator());
        break;
      case STATUS:
        Collections.sort(issues, new statusComparator());
        break;
      case PRIORITY:
        Collections.sort(issues, new priorityComparator());
        break;
      case VOTES:
        issues = issueRepository.findAllByOrderByVotes();
        break;
      case ASIGNEE:
        issues = issueRepository.findAllByOrderByCreator();
        break;
      case CREATED:
        issues = issueRepository.findAllByOrderByCreationDate();
        break;
      case UPDATED:
        issues = issueRepository.findAllByOrderByUpdatedDate();
        break;
    }

    //si el orden es ascendente (por defecto)
    if (asc_desc == null || ( asc_desc.equals("asc") && !issues.isEmpty() )) Collections.reverse(issues);

    //segundo filtramos
    if (selectStatus != null) {
      if (issues.isEmpty()) issues = issueRepository.findByStatus(selectStatus);
      else issues.retainAll(issueRepository.findByStatus(selectStatus));
    }
    if (selectPriority != null) {
      if (issues.isEmpty())
        issues = issueRepository.findByPriority(selectPriority);
      else
        issues.retainAll(issueRepository.findByPriority(selectPriority));
    }
    if (selectKind != null) {
      if (issues.isEmpty())
        issues = issueRepository.findByKind(selectKind);
      else
        issues.retainAll(issueRepository.findByKind(selectKind));
    }
    return issues;
  }

  @PostMapping("/new")
  //Coses del swagger
  @ApiOperation(value = "Create a new issue")
  //End of swagger
  @ResponseStatus(HttpStatus.CREATED)
  Issue newIssue(@Valid @RequestBody IssueDTO createNewIssue, @RequestParam(name = "token") String facebook_token){
    if (createNewIssue.getKind() == null || createNewIssue.getPriority() == null || createNewIssue.getTitle() == null){
      //comprobar que los parametros obligatorios se cumplen.
      throw new GenericException(HttpStatus.BAD_REQUEST,
          ExceptionMessages.NOT_ALL_PARAMETERS.getErrorMessage());
    }
    Session actual_session = sessionRepository.findByToken(facebook_token);
    if (actual_session == null) throw new GenericException(HttpStatus.FORBIDDEN, "There is no session with that token");
    if (!actual_session.getLoggedIn()) throw new GenericException(HttpStatus.FORBIDDEN, "You are not logged in");

    User actual_user = userRepository.findByFacebookId(actual_session.getUserId());

    Issue new_issue = new Issue(createNewIssue.getTitle(), createNewIssue.getDescription(), createNewIssue.getKind(),
        createNewIssue.getPriority(), statusEnum.NEW, new Date(), new Date(), actual_user);

    Watches new_watch = new Watches(new_issue, actual_user);
    issueRepository.save(new_issue);
    watchesRepository.save(new_watch);
    return new_issue;
  }

  @PutMapping("{id}/workflow")
  //Coses del swagger
  @ApiOperation(value = "Edit workflow of an issue")
  //End of swagger
  Issue workflow(@PathVariable Long id, @RequestParam(name = "token") String facebook_token,
      @RequestBody statusEnum status, String comment){
    Session actual_session = sessionRepository.findByToken(facebook_token);
    if (actual_session == null) throw new GenericException(HttpStatus.FORBIDDEN, "There is no session with that token");
    if (!actual_session.getLoggedIn()) throw new GenericException(HttpStatus.FORBIDDEN, "You are not logged in");

    Issue issueModified = issueRepository.findById(id).orElseThrow(
        () -> new GenericException(HttpStatus.NOT_FOUND,
        ExceptionMessages.ID_NOT_FOUND.getErrorMessage()));
    issueModified.setStatus(status);
    issueRepository.save(issueModified);
    String comment_content = "- marked as " + status + " \n " + comment;
    Comment autogenerated_comment = new Comment(comment_content, userRepository.findByFacebookId(actual_session.getUserId()), issueModified, false);
    commentRepository.save(autogenerated_comment);
    return issueModified;
  }

  @PutMapping("{id}")
//Coses del swagger
  @ApiOperation(value = "Edit an issue")
  //End of swagger
  Issue editIssue(@PathVariable Long id, @RequestParam(name = "token") String facebook_token, @RequestBody IssueDTO issueEdited){
    Session actual_session = sessionRepository.findByToken(facebook_token);
    if (actual_session == null) throw new GenericException(HttpStatus.FORBIDDEN, "There is no session with that token");
    if (!actual_session.getLoggedIn()) throw new GenericException(HttpStatus.FORBIDDEN, "You are not logged in");

    if (issueEdited.getKind() == null || issueEdited.getPriority() == null || issueEdited.getTitle() == null){
      //comprobar que los parametros obligatorios se cumplen.
      throw new GenericException(HttpStatus.BAD_REQUEST,
          ExceptionMessages.NOT_ALL_PARAMETERS.getErrorMessage());
    }

    Issue actual_issue = issueRepository.findById(id).orElseThrow(
        () -> new GenericException(HttpStatus.NOT_FOUND,
        ExceptionMessages.ID_NOT_FOUND.getErrorMessage()));

    if (actual_issue.getCreator().getFacebookToken().equals(facebook_token)) {
      //crear comentario automatico
      String comment_message = "";
      if (!actual_issue.getTitle().equals(issueEdited.getTitle()))
        comment_message += "- changed title to " + issueEdited.getTitle() + "\n";
      if (issueEdited.getStatus() != null && actual_issue.getStatus() != null && !actual_issue
          .getStatus().equals(issueEdited.getStatus()))
        comment_message += "- marked as " + issueEdited.getStatus() + "\n";
      if (!actual_issue.getKind().equals(issueEdited.getKind()))
        comment_message += "- marked as " + issueEdited.getKind() + "\n";
      if (issueEdited.getDescription() != null && actual_issue.getDescription() != null
          && !actual_issue.getDescription().equals(issueEdited.getDescription()))
        comment_message += "- edited description\n";
      if (issueEdited.getComment() != null)
        comment_message += issueEdited.getComment();

      //modificar el issue
      if (issueEdited.getStatus() != null)
        actual_issue.setStatus(issueEdited.getStatus());
      if (issueEdited.getDescription() != null)
        actual_issue.setDescription(issueEdited.getDescription());
      actual_issue.setTitle(issueEdited.getTitle());
      actual_issue.setKind(issueEdited.getKind());
      actual_issue.setPriority(issueEdited.getPriority());
      actual_issue.setUpdatedDate(new Date());

      //guardar el comentario automatico
      Comment autogenerated_comment = new Comment(comment_message,
          userRepository.findByFacebookId(actual_session.getUserId()), actual_issue, false);
      commentRepository.save(autogenerated_comment);

      //guardar el issue
      issueRepository.save(actual_issue);
      return actual_issue;
    }
    else throw new GenericException(HttpStatus.FORBIDDEN, "You can not edit an issue that does not belongs to you");
  }

  @DeleteMapping("{id}")
//Coses del swagger
  @ApiOperation(value = "Delete an issue")
  //End of swagger
  void deleteIssue(@PathVariable Long id, @RequestParam(name = "token") String facebook_token){
    Session actual_session = sessionRepository.findByToken(facebook_token);
    if (actual_session == null) throw new GenericException(HttpStatus.FORBIDDEN, "There is no session with that token");
    if (!actual_session.getLoggedIn()) throw new GenericException(HttpStatus.FORBIDDEN, "You are not logged in");

    Issue actual_issue = issueRepository.findById(id).orElseThrow(
        () -> new GenericException(HttpStatus.NOT_FOUND,
        ExceptionMessages.ID_NOT_FOUND.getErrorMessage()));

    if (actual_issue.getCreator().getFacebookToken().equals(facebook_token)) {
      //delete al comments from issue
      List<Comment> comments_from_issue = commentRepository.findByIssueCommented(actual_issue);
      for (Comment next : comments_from_issue){
        commentRepository.deleteById(next.getId());
      }

      //delete al attachments from issue
      List<Attachment> attachments_from_issue = attachmentRepository.findByIssue(actual_issue);
      for (Attachment next : attachments_from_issue) attachmentRepository.deleteById(next.getId());

      votesRepository.deleteAllByIssue(actual_issue);
      watchesRepository.deleteAllByIssue(actual_issue);


      issueRepository.deleteById(id);
    }

    else throw new GenericException(HttpStatus.FORBIDDEN, "Only the creator of the issue can delete it");
  }

  @GetMapping("/types")
//Coses del swagger
  @ApiOperation(value = "Get all possible types of issues")
  //End of swagger
  kindEnum[] getKindEnumValues(){
    return kindEnum.values();
  }

  @GetMapping("/status")
//Coses del swagger
  @ApiOperation(value = "Get all possible status of issues")
  //End of swagger
  statusEnum[] getStatusEnumValues(){
    return statusEnum.values();
  }

//Coses del swagger
  @ApiOperation(value = "Get all possible priorities of issues")
  //End of swagger
  @GetMapping("/priorities")
  priorityEnum[] getPriorityEnumValues(){
    return priorityEnum.values();
  }
  
//Coses del swagger
  @ApiOperation(value = "Get comments of an issue")
  //End of swagger
  @GetMapping("{id}/comments")
  List<Comment> getCommentsFromAnIssue(@PathVariable Long id){
    Issue issue = issueRepository.findById(id).orElseThrow(
        () -> new GenericException(HttpStatus.NOT_FOUND,
            ExceptionMessages.ID_NOT_FOUND.getErrorMessage()));
    return commentRepository.findByIssueCommented(issue);
  }
  
  @PutMapping("{id}/attachment/new")
  @ResponseStatus(HttpStatus.ACCEPTED)
//Coses del swagger
  @ApiOperation(value = "Add an attachment to an issue")
  //End of swagger
  Issue addAttachment(@PathVariable Long id, @RequestParam(name = "token") String facebook_token, @RequestBody(required = false) String comment, @RequestParam("attachment") MultipartFile attachment) throws IOException {
    Session actual_session = sessionRepository.findByToken(facebook_token);
    if (actual_session == null) throw new GenericException(HttpStatus.FORBIDDEN, "There is no session with that token");
    if (!actual_session.getLoggedIn()) throw new GenericException(HttpStatus.FORBIDDEN, "You are not logged in");

    Issue actual_issue = issueRepository.findById(id).orElseThrow(
        () -> new GenericException(HttpStatus.NOT_FOUND,
            ExceptionMessages.ID_NOT_FOUND.getErrorMessage()));

    //añadir la imagen y guardarla en la db
    if (attachmentRepository.existsByNom(attachment.getOriginalFilename())) throw new GenericException(HttpStatus.BAD_REQUEST, "This attachment already exists.");
    Attachment new_attachment = new Attachment(attachment.getBytes(), attachment.getOriginalFilename(), actual_issue);
    attachmentRepository.save(new_attachment);
    actual_issue.setUpdatedDate(new Date());

    User actual_user = userRepository.findByFacebookToken(facebook_token);
    Watches new_watch = new Watches(actual_issue, actual_user);
    watchesRepository.save(new_watch);

    //crear comentario automatico y guardarlo en la db
    String comment_content = "attached " + attachment.getOriginalFilename() + " \n" + comment;
    Comment automatic_comment = new Comment(comment_content, userRepository.findByFacebookId(actual_session.getUserId()), actual_issue, true);
    commentRepository.save(automatic_comment);

    return actual_issue;
  }

  //Coses del swagger
  @ApiOperation(value = "Get current attachment by name")
  //End of swagger
  @GetMapping("{id}/attachment")
  ResponseEntity<byte[]> getAttachment(@PathVariable Long id, @RequestParam(name = "name") String att_name) {
    Issue actual_issue = issueRepository.findById(id)
        .orElseThrow(() -> new GenericException(HttpStatus.NOT_FOUND, ExceptionMessages.ID_NOT_FOUND.getErrorMessage()));

    if (attachmentRepository.existsByNom(att_name)){
      Attachment actual_attachment = attachmentRepository.findByNom(att_name);
      byte[] imageBytes = actual_attachment.getAttachment();
      HttpHeaders responseHeader = new HttpHeaders();
      responseHeader.set("content-disposition","attachment; filename=\"" + actual_attachment.getNom() +"\"");
      return ResponseEntity.ok().headers(responseHeader).body(imageBytes);
    }
    else throw new GenericException(HttpStatus.NOT_FOUND, "Issue has no attachment");
  }
  //Coses del swagger
  @ApiOperation(value = "Delete attachment")
  //End of swagger
  @DeleteMapping("{id}/attachment")
  void deleteAttachment(@PathVariable Long id, @RequestParam(name = "token") String facebook_token, @RequestBody String att_name){
    Session actual_session = sessionRepository.findByToken(facebook_token);
    if (actual_session == null) throw new GenericException(HttpStatus.FORBIDDEN, "There is no session with that token");
    if (!actual_session.getLoggedIn()) throw new GenericException(HttpStatus.FORBIDDEN, "You are not logged in");


    Issue actual_issue = issueRepository.findById(id).orElseThrow(
        () -> new GenericException(HttpStatus.NOT_FOUND,
            ExceptionMessages.ID_NOT_FOUND.getErrorMessage()));
    if (actual_issue.getCreator().getFacebookToken().equals(facebook_token)){
      if (!attachmentRepository.existsByNom(att_name)) throw new GenericException(HttpStatus.BAD_REQUEST, "There is no attachment with that name");
      attachmentRepository.deleteByNom(att_name);
    }
    else throw new GenericException(HttpStatus.BAD_REQUEST, "You can not delete an attachment from a issue that is not yours.");
  }

  //Coses del swagger
  @ApiOperation(value = "Get current watchers")
  //End of swagger
  @GetMapping("{id}/watchs")
  int getWatchers(@PathVariable Long id){
    Issue actual_issue = issueRepository.findById(id).orElseThrow(
        () -> new GenericException(HttpStatus.NOT_FOUND,
            ExceptionMessages.ID_NOT_FOUND.getErrorMessage()));

    List<Watches> watches_from_issue = watchesRepository.findAllByIssue(actual_issue);
    return watches_from_issue.size();
  }

  //Coses del swagger
  @ApiOperation(value = "Get current votes")
  //End of swagger
  @GetMapping("{id}/votes")
  int getVotes(@PathVariable Long id){
    Issue actual_issue = issueRepository.findById(id).orElseThrow(
        () -> new GenericException(HttpStatus.NOT_FOUND,
            ExceptionMessages.ID_NOT_FOUND.getErrorMessage()));

    List<Vote> votes_from_issue = votesRepository.findAllByIssue(actual_issue);

    return votes_from_issue.size();
  }

  //Coses del swagger
  @ApiOperation(value = "Watch an issue")
  //End of swagger
  @PutMapping("{id}/watch")
  Issue watchIssue(@PathVariable Long id, @RequestParam(name = "token") String facebook_token){
    Session actual_session = sessionRepository.findByToken(facebook_token);
    if (actual_session == null) throw new GenericException(HttpStatus.FORBIDDEN, "There is no session with that token");
    if (!actual_session.getLoggedIn()) throw new GenericException(HttpStatus.FORBIDDEN, "You are not logged in");

    Issue actual_issue = issueRepository.findById(id).orElseThrow(
        () -> new GenericException(HttpStatus.NOT_FOUND,
            ExceptionMessages.ID_NOT_FOUND.getErrorMessage()));

    User actual_user = userRepository.findByFacebookToken(facebook_token);
    if (!watchesRepository.existsByUserAndIssue(actual_user, actual_issue)){
      Watches new_watch = new Watches(actual_issue, actual_user);
      watchesRepository.save(new_watch);
    }
    else throw new GenericException(HttpStatus.BAD_REQUEST, "You are already watching this issue");
    return actual_issue;
  }

  //Coses del swagger
  @ApiOperation(value = "Unwatch an issue")
  //End of swagger
  @PutMapping("{id}/unwatch")
  Issue unwatchIssue(@PathVariable Long id, @RequestParam(name = "token") String facebook_token){
    Session actual_session = sessionRepository.findByToken(facebook_token);
    if (actual_session == null) throw new GenericException(HttpStatus.FORBIDDEN, "There is no session with that token");
    if (!actual_session.getLoggedIn()) throw new GenericException(HttpStatus.FORBIDDEN, "You are not logged in");

    Issue actual_issue = issueRepository.findById(id).orElseThrow(
        () -> new GenericException(HttpStatus.NOT_FOUND,
            ExceptionMessages.ID_NOT_FOUND.getErrorMessage()));

    User actual_user = userRepository.findByFacebookToken(facebook_token);
    if (watchesRepository.existsByUserAndIssue(actual_user, actual_issue)){
      watchesRepository.deleteByUserAndIssue(actual_user, actual_issue);
    } else throw new GenericException(HttpStatus.BAD_REQUEST, "You are already watching this issue");

    return actual_issue;
  }

  //Coses del swagger
  @ApiOperation(value = "Vote an issue")
  //End of swagger
  @PutMapping("{id}/vote")
  Issue voteIssue(@PathVariable Long id, @RequestParam(name = "token") String facebook_token){
    Session actual_session = sessionRepository.findByToken(facebook_token);
    if (actual_session == null) throw new GenericException(HttpStatus.FORBIDDEN, "There is no session with that token");
    if (!actual_session.getLoggedIn()) throw new GenericException(HttpStatus.FORBIDDEN, "You are not logged in");

    Issue actual_issue = issueRepository.findById(id).orElseThrow(
        () -> new GenericException(HttpStatus.NOT_FOUND,
            ExceptionMessages.ID_NOT_FOUND.getErrorMessage()));

    User actual_user = userRepository.findByFacebookToken(facebook_token);

    if (!votesRepository.existsByUserAndIssue(actual_user, actual_issue)){
      Vote new_vote = new Vote(actual_issue, actual_user);
      votesRepository.save(new_vote);
    }
    else throw new GenericException(HttpStatus.BAD_REQUEST, "You have already voted this issue");

    if (!watchesRepository.existsByUserAndIssue(actual_user, actual_issue)){
      Watches new_watch = new Watches(actual_issue, actual_user);
      watchesRepository.save(new_watch);
    }
    List<Vote> votes = votesRepository.findAll();
    actual_issue.setVotes(votes.size());
    issueRepository.save(actual_issue);
    return actual_issue;
  }

  //Coses del swagger
  @ApiOperation(value = "Unvote an issue")
  //End of swagger
  @PutMapping("{id}/unvote")
  Issue unvoteIssue(@PathVariable Long id, @RequestParam(name = "token") String facebook_token){
    Session actual_session = sessionRepository.findByToken(facebook_token);
    if (actual_session == null) throw new GenericException(HttpStatus.FORBIDDEN, "There is no session with that token");
    if (!actual_session.getLoggedIn()) throw new GenericException(HttpStatus.FORBIDDEN, "You are not logged in");

    Issue actual_issue = issueRepository.findById(id).orElseThrow(
        () -> new GenericException(HttpStatus.NOT_FOUND,
            ExceptionMessages.ID_NOT_FOUND.getErrorMessage()));

    User actual_user = userRepository.findByFacebookToken(facebook_token);

    if (!votesRepository.existsByUserAndIssue(actual_user, actual_issue)) throw new GenericException(HttpStatus.BAD_REQUEST, "You haven't voted this issue");
    votesRepository.deleteByUserAndIssue(actual_user, actual_issue);

    List<Vote> votes = votesRepository.findAll();
    actual_issue.setVotes(votes.size());
    issueRepository.save(actual_issue);

    return actual_issue;
  }


  @ApiOperation(value = "Get all attachment names from an issue")
  @GetMapping("{id}/attachments")
  List<String> getAllAttachmentNamesFromIssue(@PathVariable Long id){
    Issue actual_issue = issueRepository.findById(id)
        .orElseThrow(() -> new GenericException(HttpStatus.NOT_FOUND, ExceptionMessages.ID_NOT_FOUND.getErrorMessage()));

    List<Attachment> attachments = attachmentRepository.findByIssue(actual_issue);
    List<String> att_names = new ArrayList<>();
    for (Attachment att : attachments){
      att_names.add(att.getNom());
    }
    return att_names;
  }

  /**
   * Comparators
   */

  class statusComparator implements Comparator<Issue> {
    @Override
    public int compare(Issue a, Issue b) {
      return a.getStatus().ordinal() > b.getStatus().ordinal() ? -1 : a == b ? 0 : 1;
    }
  }

  class kindComparator implements Comparator<Issue> {
    @Override
    public int compare(Issue a, Issue b) {
      return a.getKind().ordinal() > b.getKind().ordinal() ? -1 : a == b ? 0 : 1;
    }
  }

  class priorityComparator implements Comparator<Issue> {
    @Override
    public int compare(Issue a, Issue b) {
      return a.getPriority().ordinal() > b.getPriority().ordinal() ? -1 : a == b ? 0 : 1;
    }
  }

}
