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
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import io.devnoob.marble.persistence.entity.Impression;


public class ImpressionRepositoryTest {
 
    private Connection conn;
    private String dbPath = "testDB.db";
    private ImpressionRepository impressionRepository;

    @BeforeEach
    void setUp() throws SQLException {
        conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
        String query = "CREATE TABLE IF NOT EXISTS impression (" + 
                            "path       text PRIMARY KEY," + 
                            "marble_id  integer NOT NULL," + 
                            "type       integer NOT NULL" + 
                        ");";
        Statement statement = conn.createStatement();
        statement.execute(query);

        String query1 = "INSERT INTO impression(path, marble_id, type) VALUES(\"path1\", 1, 1);";
        String query2 = "INSERT INTO impression(path, marble_id, type) VALUES(\"path2\", 2, 2);";
        
        statement.executeUpdate(query1);
        statement.executeUpdate(query2);
        statement.close();
        
        impressionRepository = new ImpressionRepository();
        impressionRepository.setDbPath(dbPath);
        impressionRepository.connect();
    }

    @AfterEach
    void cleanUp() throws SQLException {
        conn.close();
        impressionRepository.close();
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
            assertTrue(impressionRepository.delete("path1"));
            assertTrue(impressionRepository.delete("path2"));
        }

        @Test
        void testDeleteReturnFalse() {
            assertFalse(impressionRepository.delete("path3"));
        }

        @Test
        void testIfDeleteCorrectly() throws SQLException {
            // before delete
            String query = "SELECT * FROM impression WHERE path=1;";
            Statement statement = conn.createStatement();
            ResultSet rs = statement.executeQuery(query);
            while(rs.next()) {
                assertEquals("path1", rs.getString(1));
                assertEquals(1, rs.getString(2));
                assertEquals(1, rs.getLong(3));
            }
            impressionRepository.delete("path1");

            // after delete
            rs = statement.executeQuery(query);
            assertFalse(rs.next());
        }
    }

    @Test
    void testFind() {
        assertEquals(new Impression("path1", 1L, 1), impressionRepository.find("path1"));
    }

    @Test
    void testFindAll() throws SQLException {
        List<Impression> expected = new LinkedList<>();
        String query = "SELECT * FROM impression;";
        Statement statement = conn.createStatement();
        ResultSet rs = statement.executeQuery(query);
        while(rs.next()) {
            expected.add(new Impression(rs.getString(1), rs.getLong(2), rs.getInt(3)));
        }
        assertEquals(expected, impressionRepository.findAll());
    }


    @Nested
    @DisplayName("Test insert method")
    class TestInsert {

        @Test
        void testInsert() {
            assertTrue(impressionRepository.insert(new Impression("path3", 3L, 3)));
        }

        @Test
        void testInsertCorrectly() throws SQLException {
            impressionRepository.insert(new Impression("path3", 3L, 3));
            String query = "SELECT path FROM impression WHERE path=?";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, "paht3");
            ResultSet rs = statement.executeQuery();
            while(rs.next()) {
                assertEquals("path3", rs.getString(1));
            }
        }
    }

    @Nested
    @DisplayName("Test update method")
    class TestUpdate {
        @Test
        void testUpdateReturnTrue() {
            assertTrue(impressionRepository.update(
                new Impression("path2", 2L, 2)));
        }

        @Test
        void testIfUpdateCorrectly() throws SQLException {
            Impression impression = null;

            // update
            Long updatedMarbleId = 123L;
            int updatedType = 123;

            impression = new Impression(
                "path2",
                updatedMarbleId,
                updatedType
            );


            impressionRepository.update(impression);

            // after update
            Statement statement = conn.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM impression WHERE path=\"path2\";");
            while(rs.next()) {
                assertEquals(rs.getLong(2), updatedMarbleId);
                assertEquals(rs.getInt(3), updatedType);
            }

        }
    }

}
