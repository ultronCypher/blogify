package com.rishavdas.blog.cms.repository;

import com.rishavdas.blog.cms.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

import com.rishavdas.blog.cms.dto.TopContributorDTO;

public interface PostRepository extends JpaRepository<Post,Long> {
    Post findPostByTitle(String title);
    Post findPostByTitleContaining(String title);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.author LEFT JOIN FETCH p.images WHERE p.id = :id")
    Optional<Post> findByIdWithDetails(@Param("id") Long id);

    @Query("SELECT DISTINCT p FROM Post p LEFT JOIN FETCH p.author LEFT JOIN FETCH p.images ORDER BY p.createdAt DESC")
    List<Post> findAllWithDetails();

    @Query("SELECT p FROM Post p ORDER BY p.createdAt DESC")
    List<Post> getLatestPosts();

    @Query("SELECT p FROM Post p WHERE p.author.id=:userId")
    List<Post> findPostsByUsers(@Param("userId") Long userId);

    @Query("SELECT p FROM Post p WHERE p.title LIKE %:text% OR p.content LIKE %:text%")
    List<Post> searchPostsByKeyword(@Param("text") String text);

    @EntityGraph(attributePaths = {"author", "images"})
    Page<Post> findByAuthor_Id(Long userId, Pageable pageable);

    @EntityGraph(attributePaths = {"author", "images"})
    Page<Post> findAll(Pageable pageable);

    Long countByAuthor_Id(Long userId);

    @Query("SELECT p.id FROM Post p WHERE p.author.id = :userId")
    List<Long> findPostIdsByAuthorId(@Param("userId") Long userId);

    @Query("SELECT new com.rishavdas.blog.cms.dto.TopContributorDTO(u.id, u.username, u.avatarUrl, COUNT(p)) " +
           "FROM Post p JOIN p.author u GROUP BY u.id, u.username, u.avatarUrl ORDER BY COUNT(p) DESC")
    List<TopContributorDTO> findTopContributors(Pageable pageable);
}
