package io.devnoob.marble.api;


import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;


import io.devnoob.marble.persistence.entity.User;
import io.devnoob.marble.persistence.repo.UserRepository;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserRepository userRepository;

    @Test
    void testCreateUser() {

    }

    @Test
    void testDeleteUser() {

    }

    @Nested
    @DisplayName("Test GetUserAPI")
    class GetUserAPI {
        @Test
        @DisplayName("Should return ok when user exists")
        void testGetUserShouldReturnOkWhenUserExist() throws Exception {
            Mockito.when(userRepository.find(1L))
                .thenReturn(new User(1L, "test_user1"));
    
            String url = "/api/user/1";
            mockMvc.perform(get(url)).andExpect(status().isOk());
        }


        @Test
        @DisplayName("Should return 404 when user does not exist")
        void testGetUser() throws Exception {
            Mockito.when(userRepository.find(2L)).thenReturn(null);
    
            String url = "/api/user/2";
            mockMvc.perform(get(url)).andExpect(status().isNotFound());
        }

    }

   

    @Test
    void testUpdateUser() {

    }
}
