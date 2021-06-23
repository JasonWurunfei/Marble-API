package io.devnoob.marble.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.devnoob.marble.persistence.entity.User;
import io.devnoob.marble.persistence.repo.UserRepository;

@Service
public class LoginService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Login user
     * @param username
     * @return User domain model if login is successful. Otherwise, null
     */
    public User login(String username) {
        return userRepository.find(username);
    }
}
