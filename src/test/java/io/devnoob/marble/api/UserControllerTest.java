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
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
    class testGetUserAPI {
        @Test
        @DisplayName("Should return ok when user exists")
        void getUserShouldReturnOkWhenUserExist() throws Exception {
            Mockito.when(userRepository.find(1L))
                .thenReturn(new User(1L, "test_user1"));
    
            String url = "/api/user/1";
            mockMvc.perform(get(url)).andExpect(status().isOk());
        }


        @Test
        @DisplayName("Should return 404 when user does not exist")
        void getUserShouldReturn404WhenUserDoesNotExist() throws Exception {
            Mockito.when(userRepository.find(2L)).thenReturn(null);
    
            String url = "/api/user/2";
            mockMvc.perform(get(url)).andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("GetUser shoud return correct response body")
        void getUserReturnCorrectResponseBody() throws Exception {
            User user = new User(1L, "test_user1");
            Mockito.when(userRepository.find(1L))
                .thenReturn(user);
    
            String url = "/api/user/1";
            MvcResult mvcResult = mockMvc.perform(get(url)).andReturn();
            String actualJsonResponse = mvcResult.getResponse().getContentAsString();
            String expectedJsonResponse = objectMapper.writeValueAsString(user);
            assertEquals(expectedJsonResponse, actualJsonResponse);
        }

    }

    @Test
    void testUpdateUser() {

    }
}
