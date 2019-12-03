package com.aswg12c.demo.repository;

import com.aswg12c.demo.model.Issue;
import com.aswg12c.demo.model.Session;
import com.aswg12c.demo.model.User;
import com.aswg12c.demo.model.Watches;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WatchesRepository extends JpaRepository<Watches, Long> {

  boolean existsByUserAndIssue(User actual_user, Issue actual_issue);

  List<Watches> findAllByIssue(Issue issue);

  @Transactional
  void deleteByUserAndIssue(User user, Issue actual_issue);

  @Transactional
  void deleteAllByIssue(Issue actual_issue);
}
