package com.rishavdas.blog.cms.service.impl;

import com.rishavdas.blog.cms.dto.TagDTO;
import com.rishavdas.blog.cms.model.Tag;
import com.rishavdas.blog.cms.repository.TagRepository;
import com.rishavdas.blog.cms.service.TagService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagServiceImpl implements TagService {
    private final TagRepository tagRepository;

    public TagServiceImpl(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Override
    public List<TagDTO> getAllTags() {
        List<Tag>tags=tagRepository.findAll();
        return tags.stream().map(this::convertToDTO).toList();
    }

    public TagDTO convertToDTO(Tag tag){
        TagDTO tagDTO=new TagDTO();
        tagDTO.setId(tag.getId());
        tagDTO.setName(tag.getName());
        tagDTO.setSlug(tag.getSlug());
        return tagDTO;
    }
}
