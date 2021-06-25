package io.devnoob.marble.persistence.repo;

import org.junit.jupiter.api.Test;

import io.devnoob.marble.persistence.entity.Marble;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;


public class MarbleRepositoryTest {
    
    private Connection conn;
    private String dbPath = "testDB.db";
    private MarbleRepository marbleRepository;

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
        
        marbleRepository = new MarbleRepository();
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

    @Nested
    @DisplayName("Test delete method")
    class TestDelete {

        @Test
        void testDelete() {
            assertTrue(marbleRepository.delete(1L));
            assertTrue(marbleRepository.delete(2L));
        }

        @Test
        void testDeleteReturnFalse() {
            assertFalse(marbleRepository.delete(3L));
        }

        @Test
        void testIfDeleteCorrectly() throws SQLException {
            // before delete
            String query = "SELECT * FROM marble WHERE id=1;";
            Statement statement = conn.createStatement();
            ResultSet rs = statement.executeQuery(query);
            while(rs.next()) {
                assertEquals(1L, rs.getLong(1));
                assertEquals("test_marble1", rs.getString(2));
            }
            marbleRepository.delete(1L);

            // after delete
            rs = statement.executeQuery(query);
            assertFalse(rs.next());
        }
    }

    @Test
    void testFind() {
        assertEquals(new Marble(1L, "test_marble1", 1L, new Timestamp(1623917398), "marble1_test", "story_marble1"), marbleRepository.find(1L));
    }

    @Test
    void testFindAll() throws SQLException {
        List<Marble> expected = new LinkedList<>();
        String query = "SELECT * FROM marble;";
        Statement statement = conn.createStatement();
        ResultSet rs = statement.executeQuery(query);;
        while(rs.next()) {
            expected.add(new Marble(
                rs.getLong(1), 
                rs.getString(2),
                rs.getLong(3), 
                rs.getTimestamp(4), 
                rs.getString(5), 
                rs.getString(6)
                )
            );
        }
        assertEquals(expected, marbleRepository.findAll());
    }

    @Nested
    @DisplayName("Test insert method")
    class TestInsert {
        
        @Test
        void testInsert() {
            assertTrue(marbleRepository.insert(
                new Marble("test_marble3", 3L, new Timestamp(1623917420), "marble3_test", "story_marble3")));
        }

        @Test
        void testInsertCorrectly() throws SQLException {
            marbleRepository.insert(new Marble(
                "test_marble3", 3L, new Timestamp(1623917420), "marble3_test", "story_marble3"));
            String query = "SELECT name FROM marble WHERE name=?";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, "test_marble3");
            ResultSet rs = statement.executeQuery();
            while(rs.next()) {
                assertEquals("test_marble3", rs.getString(1));
            }
        }
    }

    @Nested
    @DisplayName("Test update method")
    class TestUpdate {
        @Test
        void testUpdateReturnTrue() {
            assertTrue(marbleRepository.update(
                new Marble(2L, "test_marble3", 3L, new Timestamp(1623917420), "marble3_test", "story_marble3")));
        }

        @Test
        void testIfUpdateCorrectly() throws SQLException {
            Marble marble = null;

            // update
            String updatedName = "test_marble2_updated";
            Long updatedUserId = 123L;
            Timestamp updatedCreationTime = new Timestamp(12312312);
            String updatedTranslation = "marble2_test_updated";
            String updatedStory = "story_marble2_updated";

            marble = new Marble(
                2L,
                updatedName,
                updatedUserId,
                updatedCreationTime,
                updatedTranslation,
                updatedStory
            );


            marbleRepository.update(marble);

            // after update
            Statement statement = conn.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM marble WHERE id=2;");
            while(rs.next()) {
                assertEquals(rs.getString(2), updatedName);
                assertEquals(rs.getLong(3), updatedUserId);
                assertEquals(rs.getTimestamp(4), updatedCreationTime);
                assertEquals(rs.getString(5), updatedTranslation);
                assertEquals(rs.getString(6), updatedStory);
            }

        }
    }

    @Test
    void testGetMarblesByUserId() {
        List<Marble> expected = new LinkedList<>();
        Marble marble = new Marble(1L, "test_marble1", 1L, new Timestamp(1623917398), "marble1_test", "story_marble1");
        expected.add(marble);
        assertEquals(expected, marbleRepository.getMarblesByUserId(1L));
    }

    @Test
    void testGetLatestMarblesByUserId() {
        List<Marble> expected = new LinkedList<>();
        Marble marble = new Marble(1L, "test_marble1", 1L, new Timestamp(1623917398), "marble1_test", "story_marble1");
        expected.add(marble);
        assertEquals(expected.size(), marbleRepository.getLatestMarblesByUserId(1L, 5).size());
    }
}
