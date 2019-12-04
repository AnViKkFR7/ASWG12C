package com.aswg12c.demo.repository;

import com.aswg12c.demo.model.Issue;
import com.aswg12c.demo.model.User;
import com.aswg12c.demo.model.Vote;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VotesRepository extends JpaRepository<Vote, Long> {

  boolean existsByUserAndIssue(User user, Issue issue);

  List<Vote> findAllByIssue(Issue actual_issue);

  @Transactional
  void deleteByUserAndIssue(User actual_user, Issue issue);

  @Transactional
  void deleteAllByIssue(Issue actual_issue);
}
