package com.rishavdas.blog.cms.mapper;

import com.rishavdas.blog.cms.dto.UserDTO;
import com.rishavdas.blog.cms.model.User;

public class UserMapper {
    public static UserDTO toDTO(User user){
        if(user==null)return null;
        UserDTO dto=new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setRole(user.getRole());
        return dto;
    }
}
