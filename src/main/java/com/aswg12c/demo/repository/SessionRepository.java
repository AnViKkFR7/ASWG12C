package com.aswg12c.demo.repository;

import com.aswg12c.demo.model.Session;
import com.aswg12c.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;

public interface SessionRepository extends JpaRepository<Session, Long> {

    @Transactional
    void deleteByUserId(String userId);
}
