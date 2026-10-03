package com.rishavdas.blog.cms.repository;

import com.rishavdas.blog.cms.dto.TagWithCountDTO;
import com.rishavdas.blog.cms.model.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {
    Optional<Tag> findBySlug(String slug);
    boolean existsByName(String name);
    boolean existsBySlug(String slug);

    @Query("SELECT new com.rishavdas.blog.cms.dto.TagWithCountDTO(t.id, t.name, t.slug, COUNT(p)) " +
            "FROM Post p JOIN p.tags t GROUP BY t.id, t.name, t.slug ORDER BY COUNT(p) DESC"
    )
    List<TagWithCountDTO> findPopularTags(Pageable pageable);
}
