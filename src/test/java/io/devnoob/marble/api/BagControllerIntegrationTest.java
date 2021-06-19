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

import io.devnoob.marble.persistence.entity.Bag;
import io.devnoob.marble.persistence.repo.BagRepository;

@SpringBootTest
@AutoConfigureMockMvc
public class BagControllerIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BagRepository bagRepository;

    private Connection conn;
    private String dbPath = "testDB.db";

    @BeforeEach
    void setUp() throws SQLException {
        conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
        String query = "CREATE TABLE IF NOT EXISTS bag (" 
                            + "id         integer PRIMARY KEY,"
                            + "user_id    integer NOT NULL," 
                            + "name       text NOT NULL," 
                            + "creation_time   timestamp NOT NULL"
                        + ");";
        Statement statement = conn.createStatement();
        statement.execute(query);

        String query1 = "INSERT INTO bag(user_id, name, creation_time) VALUES(1,\"test_bag1\",1623917398);";
        String query2 = "INSERT INTO bag(user_id, name, creation_time) VALUES(2,\"test_bag2\",1623917480);";

        statement.executeUpdate(query1);
        statement.executeUpdate(query2);
        statement.close();

        bagRepository.setDbPath(dbPath);
        bagRepository.connect();
    }

    @AfterEach
    void cleanUp() throws SQLException {
        conn.close();
        bagRepository.close();
        File db = new File(dbPath);
        if (db.exists()) {
            db.delete();
        }
    }

    @Test
    void testCreateBag() throws Exception {
        Bag newBag = new Bag(1L, "test_bag3", new Timestamp(1623917399));
        String url = "/api/bag/";
        mockMvc.perform(
            post(url)
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(newBag))
        ).andExpect(status().isOk())
        .andExpect(content().string("true"));

        String query = "SELECT * FROM bag WHERE name=?;";
        PreparedStatement statement = conn.prepareStatement(query);
        statement.setString(1, "test_bag3");
        ResultSet rs = statement.executeQuery();
        while(rs.next()) {
            assertEquals(3L, rs.getLong(1));
            assertEquals(1L,  rs.getLong(2));
            assertEquals(new Timestamp(1623917399), rs.getTimestamp(4));
        }
    }

    @Test
    void testDeleteBag() throws Exception {
        String url = "/api/bag/1";
        mockMvc.perform(delete(url))
            .andExpect(status().isOk())
            .andExpect(content().string("true"));

        String query = "SELECT * FROM bag WHERE id=1;";
        Statement statement = conn.createStatement();
        ResultSet rs = statement.executeQuery(query);
        assertFalse(rs.next());
    }

    @Nested
    @DisplayName("Test GetBagAPI")
    class TestGetBagAPI {
        
        @Test
        @DisplayName("Should return ok when bag exists")
        void getBagShouldReturnOkWhenBagExist() throws Exception {
            String url = "/api/bag/marble/1";
            mockMvc.perform(get(url)).andExpect(status().isOk());
        }

        @Test
        @DisplayName("Should return 404 when bag does not exist")
        void getBagShouldReturn404WhenBagDoesNotExist() throws Exception {
            String url = "/api/bag/marble/3";
            mockMvc.perform(get(url)).andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("GetBag shoud return correct response body")
        void getBagReturnCorrectResponseBody() throws Exception {
            Bag bag = new Bag(1L, 1L, "test_bag1", new Timestamp(1623917398));
            String url = "/api/bag/marble/1";
            MvcResult mvcResult = mockMvc.perform(get(url)).andReturn();
            String actualJsonResponse = mvcResult.getResponse().getContentAsString();
            String expectedJsonResponse = objectMapper.writeValueAsString(bag);
            assertEquals(expectedJsonResponse, actualJsonResponse);
        }
    }

    @Nested
    @DisplayName("Test get bags by given user_id API")
    class TestGetBagsByUserId {

        @Test
        @DisplayName("Should return OK when user exist")
        void getBagsByUserIdShouldReturnOkWhenUserExist() throws Exception {
            String url = "/api/bag/user/1";
            mockMvc.perform(get(url)).andExpect(status().isOk());
        }

        @Test
        @DisplayName("Get bags shoud return correct response body")
        void getBagsByUserIdReturnCorrectResponseBody() throws Exception {
            List<Bag> bags = new LinkedList<>();
            bags.add(new Bag(1L, 1L, "test_bag1", new Timestamp(1623917398)));

            String url = "/api/bag/user/1";
            MvcResult mvcResult = mockMvc.perform(get(url)).andReturn();
            String actualJsonResponse = mvcResult.getResponse().getContentAsString();
            String expectedJsonResponse = objectMapper.writeValueAsString(bags);
            assertEquals(expectedJsonResponse, actualJsonResponse); 
        }
    }

    @Test
    void testUpdateBag() throws Exception {
        Bag updatedBag = new Bag(1L, 1L, "test_bag1_updated", new Timestamp(1623917398));

        String url = "/api/bag/";
        mockMvc.perform(
            put(url)
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(updatedBag))
        ).andExpect(status().isOk())
        .andExpect(content().string("true"));

        Statement statement = conn.createStatement();
        ResultSet rs = statement.executeQuery("SELECT * FROM bag WHERE id=1;");
        while(rs.next()) {
            assertEquals(rs.getString(3), updatedBag.getName());
        }
    }

    @Test
    void testBagBatchRemove() throws Exception {
        List<Long> ids = new LinkedList<>();
        ids.add(1L);
        ids.add(2L);
        String url = "/api/bag/batchremove/";
        mockMvc.perform(
            delete(url)
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(ids))
            ).andExpect(status().isOk())
            .andExpect(content().string("true"));

        String query1 = "DELETE FROM bag WHERE id IN (1,2);";
        Statement statement1 = conn.createStatement();
        statement1.executeUpdate(query1);
        
        String query2 = "SELECT * FROM bag WHERE id=1 or id=2;";
        Statement statement2 = conn.createStatement();
        ResultSet rs = statement2.executeQuery(query2);
        assertFalse(rs.next());
    }
}
