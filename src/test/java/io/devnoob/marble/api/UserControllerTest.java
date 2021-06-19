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
import static org.mockito.Mockito.times;
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
    void testCreateUser() throws Exception {
        User newUser = new User("test_user1");
        Mockito.when(userRepository.insert(newUser)).thenReturn(true);

        String url = "/api/user/";
        mockMvc.perform(
            post(url)
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(newUser))
        ).andExpect(status().isOk())
        .andExpect(content().string("true"));
        Mockito.verify(userRepository, times(1)).insert(newUser);
    }

    @Test
    void testDeleteUser() throws Exception {
        Mockito.when(userRepository.delete(1L)).thenReturn(true);
        String url = "/api/user/1";
        mockMvc.perform(delete(url))
            .andExpect(status().isOk())
            .andExpect(content().string("true"));
        Mockito.verify(userRepository, times(1)).delete(1L);
    }

    @Nested
    @DisplayName("Test GetUserAPI")
    class TestGetUserAPI {
        @Test
        @DisplayName("Should return ok when user exists")
        void getUserShouldReturnOkWhenUserExist() throws Exception {
            Mockito.when(userRepository.find(1L))
                .thenReturn(new User(1L, "test_user1"));
    
            String url = "/api/user/1";
            mockMvc.perform(get(url)).andExpect(status().isOk());
            Mockito.verify(userRepository, times(1)).find(1L);
        }


        @Test
        @DisplayName("Should return 404 when user does not exist")
        void getUserShouldReturn404WhenUserDoesNotExist() throws Exception {
            Mockito.when(userRepository.find(2L)).thenReturn(null);
    
            String url = "/api/user/2";
            mockMvc.perform(get(url)).andExpect(status().isNotFound());
            Mockito.verify(userRepository, times(1)).find(2L);
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
            Mockito.verify(userRepository, times(1)).find(1L);
        }

    }

    @Test
    void testUpdateUser() throws Exception {
        User updatedUser = new User(1L, "test_user1");
        Mockito.when(userRepository.update(updatedUser)).thenReturn(true);

        String url = "/api/user/";
        mockMvc.perform(
            put(url)
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(updatedUser))
        ).andExpect(status().isOk())
        .andExpect(content().string("true"));

        Mockito.verify(userRepository, times(1)).update(updatedUser);
    }
}
