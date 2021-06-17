package io.devnoob.marble.persistence.repo;


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
import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import io.devnoob.marble.persistence.entity.User;
import io.devnoob.marble.persistence.repo.UserRepository;

public class UserRepositoryTest {

    private Connection conn;
    private String dbPath = "testDB.db";
    private UserRepository userRepository;

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
        
        userRepository = new UserRepository();
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
    void testFind() {
        assertEquals(new User(1L, "test_user1"), userRepository.find(1L));
    }

    @Nested
    @DisplayName("Test insert method")
    class TestInsert {

        @Test
        void testInsertReturnTrue() {
            assertTrue(userRepository.insert(new User("test_user3")));
        }

        @Test
        void testInsertCorrectly() throws SQLException {
            userRepository.insert(new User("test_user3"));
            String query = "SELECT username FROM user WHERE username=?";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, "test_user3");
            ResultSet rs = statement.executeQuery();
            while(rs.next()) {
                assertEquals("test_user3",  rs.getString(1));
            }
        }

    }

    

    @Nested
    @DisplayName("Test delete method")
    class TestDelete {
        
        @Test
        void testDeleteReturnTrue() {
            assertTrue(userRepository.delete(1L));
            assertTrue(userRepository.delete(2L));
        }

        @Test
        void testDeleteReturnFalse() {
            assertFalse(userRepository.delete(3L));
        }

        @Test
        void testIfDeleteCorrectly() throws SQLException {
            // before delete
            String query = "SELECT * FROM user WHERE id=1;";
            Statement statement = conn.createStatement();
            ResultSet rs = statement.executeQuery(query);
            while(rs.next()) {
                assertEquals(1L, rs.getLong(1));
                assertEquals("test_user1", rs.getString(2));
            }
            userRepository.delete(1L);

            // after delete
            rs = statement.executeQuery(query);
            assertFalse(rs.next());
        }
    }

    @Test
    void testFindAll() throws SQLException {
        List<User> expected = new LinkedList<>();
        String query = "SELECT * FROM user;";
        Statement statement = conn.createStatement();
        ResultSet rs = statement.executeQuery(query);
        while(rs.next()) {
            expected.add(new User(rs.getLong(1), rs.getString(2)));
        }
        assertEquals(expected, userRepository.findAll());
    }
}
