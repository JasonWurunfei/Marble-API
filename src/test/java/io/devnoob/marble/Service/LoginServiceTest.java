package io.devnoob.marble.Service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import io.devnoob.marble.persistence.entity.User;
import io.devnoob.marble.persistence.repo.UserRepository;

@SpringBootTest
public class LoginServiceTest {

    @Autowired
    private LoginService loginService;

    @MockBean
    private UserRepository userRepository;

    @Test
    void testLogin() {
        String username = "test_user1";
        User user = new User(1L, username);
        Mockito.when(userRepository.find(username)).thenReturn(user);
        User actual = loginService.login(username);
        assertEquals(user, actual);
    }

    @Test
    void testLoginWhenLoginFail() {
        String username = "test_user2";
        Mockito.when(userRepository.find(username)).thenReturn(null);
        User actual = loginService.login(username);
        assertEquals(null, actual);
    }
}
