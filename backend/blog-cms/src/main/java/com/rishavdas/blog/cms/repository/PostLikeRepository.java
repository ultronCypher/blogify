package com.rishavdas.blog.cms.repository;

import com.rishavdas.blog.cms.dto.PostLikeDTO;
import com.rishavdas.blog.cms.model.Post;
import com.rishavdas.blog.cms.model.PostLike;
import com.rishavdas.blog.cms.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    boolean existsByUserAndPost(User user, Post post);
    void deleteByUserAndPost(User user,Post post);
    Long countByPost(Post post);

    @Query("""
    SELECT new com.rishavdas.blog.cms.dto.PostLikeDTO(
        p.id,
        p.title,
        p.author.username,
        COUNT(pl)
    ) FROM PostLike pl
    JOIN pl.post p
    WHERE pl.createdAt >= COALESCE(:startDate, pl.createdAt)
    GROUP BY p.id,p.title,p.author.username
    ORDER BY COUNT(pl) DESC
    """)
    List<PostLikeDTO> findTopLikedPosts(
            @Param("startDate") LocalDateTime startDate,
            Pageable pageable
    );
}
