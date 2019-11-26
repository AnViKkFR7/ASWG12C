package com.aswg12c.demo.repository;

import com.aswg12c.demo.model.Comment;
import com.aswg12c.demo.model.Issue;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

  List<Comment> findByIssueCommented(Issue issue);
}