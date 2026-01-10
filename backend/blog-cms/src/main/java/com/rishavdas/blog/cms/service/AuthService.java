package com.rishavdas.blog.cms.service;

import com.rishavdas.blog.cms.dto.LoginDTO;
import com.rishavdas.blog.cms.dto.RegisterDTO;
import com.rishavdas.blog.cms.model.User;

public interface AuthService {
    User register(User user);
    String login(String username, String password);
}
