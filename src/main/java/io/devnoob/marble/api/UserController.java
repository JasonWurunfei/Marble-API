package io.devnoob.marble.api;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.devnoob.marble.persistence.entity.User;
import io.devnoob.marble.persistence.repo.UserRepository;

@RestController
public class UserController {
    
    @Autowired
    UserRepository userRepository;

    @PostConstruct
    public void init() {
        userRepository.connect();
    }

    @GetMapping("/")
    public User getUser() {

        userRepository.insert(new User("Jason"));
        return userRepository.find(1L);
    }
}
