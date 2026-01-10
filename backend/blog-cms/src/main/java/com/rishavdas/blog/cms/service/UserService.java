package com.rishavdas.blog.cms.service;

import com.rishavdas.blog.cms.dto.UserDTO;
import com.rishavdas.blog.cms.dto.UserSummaryDTO;
import com.rishavdas.blog.cms.model.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {
    User createUser(User user);
    User getUserById(Long id);
    List<User> getAllUsers();
    User updateUser(Long id, UserDTO dto);
    void deleteUser(Long id);
    List<User> findUsernameContaining(String keyword);
    User getByUsername(String username);
    User findByUsername(String username);
    //UserSummaryDTO getUserProfile(String username);
    String uploadAvatar(MultipartFile file, String username);
    String updateAvatar(MultipartFile file, String username);
    UserSummaryDTO getPrivateProfile(String username);
    UserSummaryDTO getPublicProfile(Long userId);
}
