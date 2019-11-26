package com.aswg12c.demo.repository;
import com.aswg12c.demo.model.Issue;
import com.aswg12c.demo.model.User;
import com.aswg12c.demo.model.kindEnum;
import com.aswg12c.demo.model.priorityEnum;
import com.aswg12c.demo.model.statusEnum;
import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface IssueRepository extends JpaRepository<Issue, Long> {


  List<Issue> findByStatus(statusEnum status);

  List<Issue> findByKind(kindEnum kind);

  List<Issue> findByPriority(priorityEnum priority);

  List<Issue> findAllByOrderByVotes();

  List<Issue> findAllByOrderByCreationDate();

  List<Issue> findAllByOrderByUpdatedDate();

  List<Issue> findAllByOrderByCreator();
}