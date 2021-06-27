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
import io.devnoob.marble.persistence.entity.Marble;
import io.devnoob.marble.persistence.repo.BagRepository;
import io.devnoob.marble.persistence.repo.MarbleBagRepository;

@SpringBootTest
@AutoConfigureMockMvc
public class BagControllerIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private BagRepository bagRepository;

    @Autowired
    private MarbleBagRepository marbleBagRepository;

    private Connection conn;
    private String dbPath = "testDB.db";

    @BeforeEach
    void setUp() throws SQLException {
        conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
        String sql = "CREATE TABLE IF NOT EXISTS marble_bag (" + 
                            "id         integer PRIMARY KEY," + 
                            "marble_id  integer NOT NULL," +
                            "bag_id     integer NOT NULL" +
                        ");";
        Statement statement = conn.createStatement();
        statement.execute(sql);
        
        String query;
        query = "INSERT INTO marble_bag(marble_id, bag_id) VALUES(1, 1);";
        statement.executeUpdate(query);
        query = "INSERT INTO marble_bag(marble_id, bag_id) VALUES(2, 1);";
        statement.executeUpdate(query);
        query = "INSERT INTO marble_bag(marble_id, bag_id) VALUES(3, 1);";
        statement.executeUpdate(query);
        query = "INSERT INTO marble_bag(marble_id, bag_id) VALUES(3, 2);";
        statement.executeUpdate(query);
        query = "INSERT INTO marble_bag(marble_id, bag_id) VALUES(1, 2);";
        statement.executeUpdate(query);

        sql ="CREATE TABLE IF NOT EXISTS bag (" 
                + "id         integer PRIMARY KEY,"
                + "user_id    integer NOT NULL," 
                + "name       text NOT NULL," 
                + "creation_time   timestamp NOT NULL"
            + ");";
        statement = conn.createStatement();
        statement.execute(sql); 

        query = "INSERT INTO bag(user_id, name, creation_time) VALUES(1,\"test_bag1\",1623917398);";
        statement.executeUpdate(query);
        query = "INSERT INTO bag(user_id, name, creation_time) VALUES(1,\"test_bag2\",1623917358);";
        statement.executeUpdate(query);

        sql = "CREATE TABLE IF NOT EXISTS marble (" + 
                    "id             integer PRIMARY KEY," + 
                    "name           text NOT NULL," + 
                    "user_id        integer NOT NULL," + 
                    "creation_time  timestamp NOT NULL," +
                    "translation    text NOT NULL," +
                    "story          text NOT NULL" +   
              ");";
        statement = conn.createStatement();
        statement.execute(sql); 

        query = "INSERT INTO marble(name, user_id, creation_time, translation, story) "+
            "VALUES(\"test_marble1\", 1, 1623917398, \"marble1_test\", \"story_marble1\");";
        statement.executeUpdate(query);
        query = "INSERT INTO marble(name, user_id, creation_time, translation, story) "+
            "VALUES(\"test_marble2\", 2, 1623917480, \"marble2_test\", \"story_marble2\");";
        statement.executeUpdate(query);
        query = "INSERT INTO marble(name, user_id, creation_time, translation, story) "+
            "VALUES(\"test_marble3\", 2, 1623912280, \"marble3_test\", \"story_marble3\");";
        statement.executeUpdate(query);

        statement.close();


        bagRepository.setDbPath(dbPath);
        bagRepository.connect();
        
        marbleBagRepository.setDbPath(dbPath);
        marbleBagRepository.connect();
    }

    @AfterEach
    void cleanUp() throws SQLException {
        conn.close();
        bagRepository.close();
        marbleBagRepository.close();
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
            assertEquals(newBag.getCreationTime(), rs.getTimestamp(4));
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
            bags.add(new Bag(2L, 1L, "test_bag2", new Timestamp(1623917358)));
            String url = "/api/bag/user/1";
            MvcResult mvcResult = mockMvc.perform(get(url)).andReturn();
            String actualJsonResponse = mvcResult.getResponse().getContentAsString();
            String expectedJsonResponse = objectMapper.writeValueAsString(bags);
            assertEquals(expectedJsonResponse, actualJsonResponse); 
        }
    }

    @Test
    void testUpdateBag() throws Exception {
        Bag updatedBag = new Bag(1L, 3L, "test_bag1_updated", new Timestamp(1623917331));

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
            assertEquals(rs.getLong(2), updatedBag.getUserId());
            assertEquals(rs.getString(3), updatedBag.getName());
            assertEquals(rs.getTimestamp(4), updatedBag.getCreationTime());
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

    @Nested
    @DisplayName("Test get marbles by given bag id API")
    class TestGetMarbleByBagId {
        @Test
        @DisplayName("Should return 200 OK if bag id is valid")
        void shouldReturn200OKIfBagIdIsValid() throws Exception {
            String url = "/api/bag/marbles/1";
            mockMvc.perform(get(url)).andExpect(status().isOk());
        }

        @Test
        @DisplayName("Should return 404 not found if bag id is not valid")
        void shouldReturn404NotFoundIfBagIdIsNotValid() throws Exception {
            String url = "/api/bag/marbles/100";
            mockMvc.perform(get(url)).andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should return correct marble list")
        void shouldReturnCorrectMarbles() throws Exception {

            Marble marble1 = new Marble(1L, "test_marble1", 1L, new Timestamp(1623917398), "marble1_test", "story_marble1");
            Marble marble2 = new Marble(2L, "test_marble2", 2L, new Timestamp(1623917480), "marble2_test", "story_marble2");
            Marble marble3 = new Marble(3L, "test_marble3", 2L, new Timestamp(1623912280), "marble3_test", "story_marble3");

            List<Marble> expected = new LinkedList<>();
            expected.add(marble1);
            expected.add(marble2);
            expected.add(marble3);

            String url = "/api/bag/marbles/1";
            MvcResult mvcResult = mockMvc.perform(get(url)).andReturn();
            String actualJsonResponse = mvcResult.getResponse().getContentAsString();
            String expectedJsonResponse = objectMapper.writeValueAsString(expected);
            assertEquals(expectedJsonResponse, actualJsonResponse); 


            expected = new LinkedList<>();
            expected.add(marble1);
            expected.add(marble3);

            url = "/api/bag/marbles/2";
            mvcResult = mockMvc.perform(get(url)).andReturn();
            actualJsonResponse = mvcResult.getResponse().getContentAsString();
            expectedJsonResponse = objectMapper.writeValueAsString(expected);
            assertEquals(expectedJsonResponse, actualJsonResponse); 
        }
    }
}
