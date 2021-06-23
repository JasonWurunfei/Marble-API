package io.devnoob.marble.api;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @GetMapping("/{user_id}")
    public ResponseEntity<User> getUser(@PathVariable Long user_id) {
        User user = userRepository.find(user_id);
        if (user == null)
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PostMapping("/")
    public boolean createUser(@RequestBody User user) {
        return userRepository.insert(user);
    }

    @DeleteMapping("/{user_id}")
    public boolean deleteUser(@PathVariable Long user_id) {
        return userRepository.delete(user_id);
    }

    @PutMapping("/")
    public boolean updateUser(@RequestBody User user) {
        return userRepository.update(user);
    }

    @GetMapping("/login")
    public ResponseEntity<User> login(@RequestParam("username") String username) {
        User user = userRepository.find(username);
        if (user == null)
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }
}
