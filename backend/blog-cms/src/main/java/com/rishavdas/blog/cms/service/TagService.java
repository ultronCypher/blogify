package com.rishavdas.blog.cms.service;

import com.rishavdas.blog.cms.dto.TagDTO;
import com.rishavdas.blog.cms.model.Tag;

import java.util.List;

public interface TagService {
    List<TagDTO> getAllTags();
}
