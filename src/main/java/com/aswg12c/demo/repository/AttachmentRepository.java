package com.aswg12c.demo.repository;

import com.aswg12c.demo.model.Attachment;
import com.aswg12c.demo.model.Comment;
import com.aswg12c.demo.model.Issue;
import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttachmentRepository extends JpaRepository<Attachment, Long> {

  @Transactional
  List<Attachment> findByIssue(Issue issue);

  @Transactional
  boolean existsByNom(String originalFilename);

  @Transactional
  Attachment findByNom(String originalFilename);

  @Transactional
  void deleteByNom(String att_name);
}
