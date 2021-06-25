package io.devnoob.marble.api;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;

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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import io.devnoob.marble.persistence.entity.Marble;
import io.devnoob.marble.persistence.repo.MarbleRepository;

@SpringBootTest
@AutoConfigureMockMvc
public class MarbleControllerIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MarbleRepository marbleRepository;

    private Connection conn;
    private String dbPath = "testDB.db";

    @BeforeEach
    void setUp() throws SQLException {
        conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
        String query = "CREATE TABLE IF NOT EXISTS marble (" + 
                       "id         integer PRIMARY KEY," + 
                       "name       text NOT NULL," + 
                       "user_id    integer NOT NULL," + 
                       "creation_time   timestamp NOT NULL," +
                       "translation     text NOT NULL," +
                       "story      text NOT NULL" +   
                ");";
        Statement statement = conn.createStatement();
        statement.execute(query);

        String query1 = "INSERT INTO marble(name, user_id, creation_time, translation, story) VALUES(\"test_marble1\", 1, 1623917398, \"marble1_test\", \"story_marble1\");";
        String query2 = "INSERT INTO marble(name, user_id, creation_time, translation, story) VALUES(\"test_marble2\", 2, 1623917480, \"marble2_test\", \"story_marble2\");";
        
        statement.executeUpdate(query1);
        statement.executeUpdate(query2);
        statement.close();
        
        marbleRepository.setDbPath(dbPath);
        marbleRepository.connect();
    }

    @AfterEach
    void cleanUp() throws SQLException {
        conn.close();
        marbleRepository.close();
        File db = new File(dbPath);
        if (db.exists()) {
            db.delete();
        }
    }

    @Test
    void testCreateMarble() throws Exception {
        Marble newMarble = new Marble("test_marble3", 1L, new Timestamp(1623917398), "marble3_test", "story_marble3");
        String url = "/api/marble/";
        mockMvc.perform(
            post(url)
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(newMarble))
        ).andExpect(status().isOk())
        .andExpect(content().string("true"));

        String query = "SELECT * FROM marble WHERE name=?;";
        PreparedStatement statement = conn.prepareStatement(query);
        statement.setString(1, "test_marlble3");
        ResultSet rs = statement.executeQuery();
        while(rs.next()) {
            assertEquals(3L, rs.getLong(1));
            assertEquals(1L,  rs.getLong(3));
            assertEquals(new Timestamp(1623917398), rs.getTimestamp(4));
            assertEquals("marble3_test", rs.getString(5));
            assertEquals("story_marble3", rs.getString(6));
        }
    }

    @Test
    void testDeleteMarble() throws Exception {
        String url = "/api/marble/1";
        mockMvc.perform(delete(url))
            .andExpect(status().isOk())
            .andExpect(content().string("true"));

        String query = "SELECT * FROM marble WHERE id=1;";
        Statement statement = conn.createStatement();
        ResultSet rs = statement.executeQuery(query);
        assertFalse(rs.next());
    }

    @Nested
    @DisplayName("Test GetMarbleAPI")
    class TestGetMarbleAPI {
        
        @Test
        @DisplayName("Should return ok when marble exists")
        void getMarbleShouldReturnOkWhenMarbleExist() throws Exception {
            String url = "/api/marble/1";
            mockMvc.perform(get(url)).andExpect(status().isOk());
        }

        @Test
        @DisplayName("Should return 404 when marble does not exist")
        void getMarbleShouldReturn404WhenMarbleDoesNotExist() throws Exception {
            String url = "/api/marble/3";
            mockMvc.perform(get(url)).andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("GetMarble shoud return correct response body")
        void getMarbleReturnCorrectResponseBody() throws Exception {
            Marble marble = new Marble(1L, "test_marble1", 1L, new Timestamp(1623917398), "marble1_test", "story_marble1");
            String url = "/api/marble/1";
            MvcResult mvcResult = mockMvc.perform(get(url)).andReturn();
            String actualJsonResponse = mvcResult.getResponse().getContentAsString();
            String expectedJsonResponse = objectMapper.writeValueAsString(marble);
            assertEquals(expectedJsonResponse, actualJsonResponse);
        }
    }
    @Nested
    @DisplayName("Test get marbles by given user_id API")
    class TestGetMarblesByUserId {

        @Test
        @DisplayName("Should return OK when user exist")
        void getMarblesByUserIdShouldReturnOkWhenUserExist() throws Exception {
            String url = "/api/marble/user/1";
            mockMvc.perform(get(url)).andExpect(status().isOk());
        }

        @Test
        @DisplayName("Get marbles shoud return correct response body")
        void getMarblesByUserIdReturnCorrectResponseBody() throws Exception {
            List<Marble> marbles = new LinkedList<>();
            marbles.add(new Marble(1L, "test_marble1", 1L, new Timestamp(1623917398), "marble1_test", "story_marble1"));

            String url = "/api/marble/user/1";
            MvcResult mvcResult = mockMvc.perform(get(url)).andReturn();
            String actualJsonResponse = mvcResult.getResponse().getContentAsString();
            String expectedJsonResponse = objectMapper.writeValueAsString(marbles);
            assertEquals(expectedJsonResponse, actualJsonResponse); 
        }

        @Test
        @DisplayName("Should return OK and in the form of DESC when user exist")
        void getLatestMarblesByUserIdShouldReturnOkWhenUserExist() throws Exception {
            String url = "/api/marble/latest/1?limit=2";
            mockMvc.perform(get(url)).andExpect(status().isOk());
        }

        @Test
        @DisplayName("Get marbles shoud return correct response body")
        void getLatestMarblesByUserIdShouldReturnOkAndMatchableNumberWhenUserExist() throws Exception {
            List<Marble> marbles = new LinkedList<>();
            marbles.add(new Marble(1L, "test_marble1", 1L, new Timestamp(1623917398), "marble1_test", "story_marble1"));
            marbles.add(new Marble(1L, "test_marble2", 1L, new Timestamp(1623917377), "marble2_test", "story_marble2"));

            String url = "/api/marble/latest/1?limit=2";
            MvcResult mvcResult = mockMvc.perform(get(url)).andReturn();
            String actualJsonResponse = mvcResult.getResponse().getContentAsString();
            String expectedJsonResponse = objectMapper.writeValueAsString(marbles);
            assertEquals(expectedJsonResponse, actualJsonResponse);
        }

        @Test
        @DisplayName("Should return OK and matchable number when user exist")
        void getLatestMarblesByUserIdShouldReturnOkAndMatchableNumberWhenMore() throws Exception {
            List<Marble> marbles = new LinkedList<>();
            marbles.add(new Marble(1L, "test_marble1", 1L, new Timestamp(1623917398), "marble1_test", "story_marble1"));
            marbles.add(new Marble(1L, "test_marble2", 1L, new Timestamp(1623917377), "marble2_test", "story_marble2"));

            String url = "/api/marble/latest/1?limit=100";
            MvcResult mvcResult = mockMvc.perform(get(url)).andReturn();
            String actualJsonResponse = mvcResult.getResponse().getContentAsString();
            String expectedJsonResponse = objectMapper.writeValueAsString(marbles);
            assertEquals(expectedJsonResponse, actualJsonResponse);
        }
    }

    @Test
    void testUpdateMarble() throws Exception {
        Marble updatedMarble = new Marble(1L, "test_marble1_updated", 2L, new Timestamp(1623917331), "marble1_test_updated", "story_marble1_updated");

        String url = "/api/marble/";
        mockMvc.perform(
            put(url)
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(updatedMarble))
        ).andExpect(status().isOk())
        .andExpect(content().string("true"));

        Statement statement = conn.createStatement();
        ResultSet rs = statement.executeQuery("SELECT * FROM marble WHERE id=1;");
        while(rs.next()) {
            assertEquals(rs.getString(2), updatedMarble.getName());
            assertEquals(rs.getLong(3), updatedMarble.getUserId());
            assertEquals(rs.getTimestamp(4), updatedMarble.getCreationTime());
            assertEquals(rs.getString(5), updatedMarble.getTranslation());
            assertEquals(rs.getString(6), updatedMarble.getStory());
        }
    }
    @Test
    void testMarbleBatchRemove() throws Exception {
        List<Long> ids = new LinkedList<>();
        ids.add(1L);
        ids.add(2L);
        String url = "/api/marble/batchremove/";
        mockMvc.perform(
            delete(url)
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(ids))
            ).andExpect(status().isOk())
            .andExpect(content().string("true"));

        String query1 = "DELETE FROM marble WHERE id IN (1,2);";
        Statement statement1 = conn.createStatement();
        statement1.executeUpdate(query1);
        
        String query2 = "SELECT * FROM marble WHERE id=1 or id=2;";
        Statement statement2 = conn.createStatement();
        ResultSet rs = statement2.executeQuery(query2);
        assertFalse(rs.next());
    }
}
