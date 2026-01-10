package com.rishavdas.blog.cms;

import com.rishavdas.blog.cms.model.User;
import com.rishavdas.blog.cms.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class UserTests {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testUserRepository(){
        List<User> userList = userRepository.findAll();
        System.out.println(userList);
    }
}
