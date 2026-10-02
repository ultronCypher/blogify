package com.rishavdas.blog.cms.dto.post_dtos;

import com.rishavdas.blog.cms.dto.TagDTO;
import com.rishavdas.blog.cms.dto.UserDTO;

import java.util.List;

public class PostResponseDTO {
    private Long id;
    private String title;
    private String content;
    private UserDTO author;
    private List<String> images;
    private List<TagDTO> tags;
}
