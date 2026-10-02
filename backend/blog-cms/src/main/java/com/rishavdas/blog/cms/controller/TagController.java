package com.rishavdas.blog.cms.controller;

import com.rishavdas.blog.cms.dto.TagDTO;
import com.rishavdas.blog.cms.service.TagService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
public class TagController {
    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping
    public ResponseEntity<List<TagDTO>> getAllTags(){
        return ResponseEntity.ok(tagService.getAllTags());
    }
}
