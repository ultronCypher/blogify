package com.rishavdas.blog.cms.repository;

import com.rishavdas.blog.cms.model.Post;
import com.rishavdas.blog.cms.model.PostView;
import com.rishavdas.blog.cms.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

public interface PostViewRepository extends JpaRepository<PostView, Long> {
    @Query("SELECT COUNT(pv) FROM PostView pv WHERE pv.post.id = :postId")
    Long countByPostId(@Param("postId") Long postId);
    boolean existsByPostAndUser(Post post, User user);

    @Query("SELECT COUNT(pv) FROM PostView pv WHERE pv.post.author.id = :userId")
    Long countTotalViewsByAuthorId(@Param("userId") Long userId);

    @Modifying
    @Transactional
    @Query("DELETE FROM PostView pv WHERE pv.post.id = :postId")
    void deleteByPostId(@Param("postId") Long postId);
}
