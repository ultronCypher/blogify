package com.rishavdas.blog.cms.repository;

import com.rishavdas.blog.cms.model.Role;
import com.rishavdas.blog.cms.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    List<User> findByUsernameContaining(String keyword);
    List<User> findByRole(Role role);
    Optional<User> findByEmail(String email);
    List<User> findByUsernameOrEmail(String username,String email);

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    Optional<User> findByUsername(String username);

    @Query("SELECT u FROM User u WHERE LOWER(u.username) LIKE LOWER(CONCAT('%', :query, '%')) ORDER BY u.username ASC")
    List<User> searchUsers(@Param("query") String query, Pageable pageable);
}
