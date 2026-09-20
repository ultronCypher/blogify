package com.rishavdas.blog.cms.controller;

import com.rishavdas.blog.cms.dto.UserDTO;
import com.rishavdas.blog.cms.dto.UserSummaryDTO;
import com.rishavdas.blog.cms.mapper.UserMapper;
import com.rishavdas.blog.cms.model.User;
import com.rishavdas.blog.cms.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<UserDTO> createUser(@RequestBody User user){
        User saved=userService.createUser(user);
        return ResponseEntity.ok(UserMapper.toDTO(saved));
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers(){
        List<User>users=userService.getAllUsers();
        List<UserDTO>dtos=users.stream()
                .map(UserMapper::toDTO)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody UserDTO dto){
        User updated=userService.updateUser(id,dto);
        return ResponseEntity.ok(UserMapper.toDTO(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id){
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserDTO>> searchUsers(@RequestParam String keyword){
        List<User> users=userService.findUsernameContaining(keyword);
        List<UserDTO>dtos=users.stream()
                .map(UserMapper::toDTO)
                .toList();
        return  ResponseEntity.ok(dtos);
    }

//    @GetMapping("/users/{userId}")
//    public ResponseEntity<UserSummaryDTO> getUserProfile(
//            @PathVariable Long userId
//    ){
//        String username=userService.getUserById(userId).getUsername();
//        UserSummaryDTO dto=userService.getUserProfile(username);
//        return ResponseEntity.ok(dto);
//    }

    @PostMapping("/me/avatar")
    public ResponseEntity<?> uploadAvatar(
            @RequestParam("file")MultipartFile file,
            Authentication authentication
    ){

        String avatarUrl= userService.uploadAvatar(file, authentication.getName());
        return ResponseEntity.ok(Map.of("avatarUrl",avatarUrl));
    }

    @GetMapping("/me")
    public ResponseEntity<UserSummaryDTO> getMyProfile(
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                userService.getPrivateProfile(authentication.getName())
        );
    }
    @GetMapping("/{userId}")
    public ResponseEntity<UserSummaryDTO> getPublicProfile(
            @PathVariable Long userId
    ) {
        return ResponseEntity.ok(
                userService.getPublicProfile(userId)
        );
    }
}
