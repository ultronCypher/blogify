package com.rishavdas.blog.cms.controller;

import com.rishavdas.blog.cms.dto.GlobalSearchResultDTO;
import com.rishavdas.blog.cms.service.SearchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping
    public ResponseEntity<GlobalSearchResultDTO> globalSearch(
            @RequestParam(name = "q", required = false, defaultValue = "") String query,
            @RequestParam(name = "limit", required = false, defaultValue = "5") int limit
    ) {
        return ResponseEntity.ok(searchService.globalSearch(query, limit));
    }
}
