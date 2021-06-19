package io.devnoob.marble.api;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import io.devnoob.marble.persistence.entity.User;
import io.devnoob.marble.persistence.repo.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    private Connection conn;
    private String dbPath = "testDB.db";

    @BeforeEach
    void setUp() throws SQLException {
        conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
        String query = "CREATE TABLE IF NOT EXISTS user (" +
                        "id         integer PRIMARY KEY," +
                        "username   text NOT NULL" +
                    ");";
        Statement statement = conn.createStatement();
        statement.execute(query);

        String query1 = "INSERT INTO user(username) VALUES(\"test_user1\");";
        String query2 = "INSERT INTO user(username) VALUES(\"test_user2\");";
        
        statement.executeUpdate(query1);
        statement.executeUpdate(query2);
        statement.close();
        
        userRepository.setDbPath(dbPath);
        userRepository.connect();
    }

    @AfterEach
    void cleanUp() throws SQLException {
        conn.close();
        userRepository.close();
        File db = new File(dbPath);
        if (db.exists()) {
            db.delete();
        }
    }

    @Test
    void testCreateUser() throws Exception {
        User newUser = new User("test_user1");
        String url = "/api/user/";
        mockMvc.perform(
            post(url)
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(newUser))
        ).andExpect(status().isOk())
        .andExpect(content().string("true"));
    }

    @Test
    void testDeleteUser() throws Exception {
        String url = "/api/user/1";
        mockMvc.perform(delete(url))
            .andExpect(status().isOk())
            .andExpect(content().string("true"));
    }

    @Nested
    @DisplayName("Test GetUserAPI")
    class TestGetUserAPI {
        @Test
        @DisplayName("Should return ok when user exists")
        void getUserShouldReturnOkWhenUserExist() throws Exception {
            String url = "/api/user/1";
            mockMvc.perform(get(url)).andExpect(status().isOk());
        }

        @Test
        @DisplayName("Should return 404 when user does not exist")
        void getUserShouldReturn404WhenUserDoesNotExist() throws Exception {
            String url = "/api/user/3";
            mockMvc.perform(get(url)).andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("GetUser shoud return correct response body")
        void getUserReturnCorrectResponseBody() throws Exception {
            User user = new User(1L, "test_user1");
            String url = "/api/user/1";
            MvcResult mvcResult = mockMvc.perform(get(url)).andReturn();
            String actualJsonResponse = mvcResult.getResponse().getContentAsString();
            String expectedJsonResponse = objectMapper.writeValueAsString(user);
            assertEquals(expectedJsonResponse, actualJsonResponse);
        }
    }

    @Test
    void testUpdateUser() throws Exception {
        User updatedUser = new User(1L, "test_user1");

        String url = "/api/user/";
        mockMvc.perform(
            put(url)
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(updatedUser))
        ).andExpect(status().isOk())
        .andExpect(content().string("true"));
    }
}
