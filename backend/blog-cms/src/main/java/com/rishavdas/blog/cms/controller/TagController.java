package com.rishavdas.blog.cms.controller;

import com.rishavdas.blog.cms.dto.TagDTO;
import com.rishavdas.blog.cms.dto.TagWithCountDTO;
import com.rishavdas.blog.cms.repository.TagRepository;
import com.rishavdas.blog.cms.service.TagService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
public class TagController {
    private final TagService tagService;
    private final TagRepository tagRepository;

    public TagController(TagService tagService, TagRepository tagRepository) {
        this.tagService = tagService;
        this.tagRepository = tagRepository;
    }

    @GetMapping
    public ResponseEntity<List<TagDTO>> getAllTags(){
        return ResponseEntity.ok(tagService.getAllTags());
    }

    @GetMapping("/popular")
    public ResponseEntity<List<TagWithCountDTO>> getPopularTags(@RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(tagService.getPopularTags(limit));
    }
}
