package io.devnoob.marble.api;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.devnoob.marble.persistence.entity.User;
import io.devnoob.marble.persistence.repo.UserRepository;

@RestController
@RequestMapping("/api/user")
public class UserController {
    
    @Autowired
    UserRepository userRepository;

    @PostConstruct
    public void init() {
        userRepository.connect();
        if (userRepository.findAll().size() == 0) {
            userRepository.insert(new User("Jason"));
            userRepository.insert(new User("Zed"));
        }
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) {
        return userRepository.find(id);
    }

    @PostMapping("/")
    public boolean createUser(@RequestBody User user) {
        return userRepository.insert(user);
    }

    @DeleteMapping("/{id}")
    public boolean deleteUser(@PathVariable Long id) {
        return userRepository.delete(id);
    }

    @PutMapping("/")
    public boolean updateUser(@RequestBody User user) {
        return userRepository.update(user);
    }

}
