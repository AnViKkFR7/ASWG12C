package com.aswg12c.demo.repository;
    import com.aswg12c.demo.model.User;
    import java.util.List;
    import java.util.Set;
    import org.springframework.data.jpa.repository.JpaRepository;
    import org.springframework.data.jpa.repository.Query;
    import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {


  User findByFacebookId(String userId);

  User findByFacebookToken(String facebook_token);
}