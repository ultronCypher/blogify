package com.rishavdas.blog.cms.service;

import com.rishavdas.blog.cms.dto.GlobalSearchResultDTO;

public interface SearchService {
    GlobalSearchResultDTO globalSearch(String query, int limit);
}
