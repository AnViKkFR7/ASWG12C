package com.aswg12c.demo.controller;

import com.aswg12c.demo.exceptions.ExceptionMessages;
import com.aswg12c.demo.exceptions.GenericException;
import com.aswg12c.demo.model.Comment;
import com.aswg12c.demo.model.Issue;
import com.aswg12c.demo.model.kindEnum;
import com.aswg12c.demo.model.priorityEnum;
import com.aswg12c.demo.model.sortEnum;
import com.aswg12c.demo.model.statusEnum;
import com.aswg12c.demo.repository.CommentRepository;
import com.aswg12c.demo.repository.IssueRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
@RequestMapping("/api" + IssueController.PATH)
public class IssueController {
  public static final String PATH = "/issues";

  @Autowired
  private IssueRepository issueRepository;

  @Autowired
  private CommentRepository commentRepository;

  @GetMapping("/{id}") //coger un issue con un id concreto
  Issue getIssue(@PathVariable Long id){
    return issueRepository.findById(id).orElseThrow(
                    () -> new GenericException(HttpStatus.FORBIDDEN,
                        ExceptionMessages.ID_NOT_FOUND.getErrorMessage()));

  }

  //los select son el get de cuando le clicas en el tipo de issue en la tabla y te salen solo los de ese tipo (es acumulativo).
  //los order son de cuando le das en la tabla a ordenarlos según el tipo.
  @GetMapping()
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

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  Issue newIssue(@Valid @RequestBody Issue createNewIssue){
    if (createNewIssue.getKind() == null || createNewIssue.getPriority() == null || createNewIssue.getTitle() == null){
      //comprobar que los parametros obligatorios se cumplen.
      throw new GenericException(HttpStatus.BAD_REQUEST,
          ExceptionMessages.NOT_ALL_PARAMETERS.getErrorMessage());
    }
      createNewIssue.setStatus(statusEnum.NEW); //al crear una issue el status es NEW
      createNewIssue.setVotes(1); //al crear un issue se pone watchers a 1 automáticamente.
      issueRepository.save(createNewIssue);
      return createNewIssue;
  }

  @PutMapping("{id}")
  Issue workflow(@PathVariable Long id, @RequestBody(required = false) statusEnum status){
    Issue issueModified = issueRepository.findById(id).orElseThrow(
        () -> new GenericException(HttpStatus.FORBIDDEN,
        ExceptionMessages.ID_NOT_FOUND.getErrorMessage()));
    issueModified.setStatus(status);
    issueRepository.deleteById(id);
    issueRepository.save(issueModified);
    return issueModified;
  }

  @PutMapping("{id}/edit")
  Issue editIssue(@PathVariable Long id, @RequestBody Issue issueEdited){
    if (issueEdited.getKind() == null || issueEdited.getPriority() == null || issueEdited.getTitle() == null){
      //comprobar que los parametros obligatorios se cumplen.
      throw new GenericException(HttpStatus.BAD_REQUEST,
          ExceptionMessages.NOT_ALL_PARAMETERS.getErrorMessage());
    }
    issueRepository.deleteById(id);
    issueRepository.save(issueEdited);
    return issueEdited;
  }

  @DeleteMapping("{id}")
  void deleteIssue(@PathVariable Long id){
    //hago esto por que no se que devuelve si se intent eliminar un issue que no existe.
    Issue issueDeleted = issueRepository.findById(id).orElseThrow(
        () -> new GenericException(HttpStatus.FORBIDDEN,
            ExceptionMessages.ID_NOT_FOUND.getErrorMessage()));
    issueRepository.deleteById(id);
  }

  @GetMapping("/kindEnum")
  kindEnum[] getKindEnumValues(){
    return kindEnum.values();
  }

  @GetMapping("/statusEnum")
  statusEnum[] getStatusEnumValues(){
    return statusEnum.values();
  }

  @GetMapping("/priorityEnum")
  priorityEnum[] getPriorityEnumValues(){
    return priorityEnum.values();
  }

  @GetMapping("{id}/comments")
  List<Comment> getCommentsFromAnIssue(@PathVariable Long id){
    Issue issue = issueRepository.findById(id).orElseThrow(
        () -> new GenericException(HttpStatus.FORBIDDEN,
            ExceptionMessages.ID_NOT_FOUND.getErrorMessage()));
    return commentRepository.findByIssueCommented(issue);
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
