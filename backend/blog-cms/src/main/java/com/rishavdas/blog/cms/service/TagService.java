package com.rishavdas.blog.cms.service;

import com.rishavdas.blog.cms.dto.TagDTO;
import com.rishavdas.blog.cms.dto.TagWithCountDTO;

import java.util.List;

public interface TagService {
    List<TagDTO> getAllTags();
    List<TagWithCountDTO> getPopularTags(int limit);
}
