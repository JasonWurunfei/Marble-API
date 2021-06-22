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

import io.devnoob.marble.persistence.entity.Impression;


public class ImpressionRepositoryTest {
 
    private Connection conn;
    private String dbPath = "testDB.db";
    private ImpressionRepository impressionRepository;

    @BeforeEach
    void setUp() throws SQLException {
        conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
        String query = "CREATE TABLE IF NOT EXISTS impression (" + 
                            "id         integer PRIMARY KEY," + 
                            "path       text NOT NULL," + 
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
            assertTrue(impressionRepository.delete(1L));
            assertTrue(impressionRepository.delete(2L));
        }

        @Test
        void testDeleteReturnFalse() {
            assertFalse(impressionRepository.delete(3L));
        }

        @Test
        void testIfDeleteCorrectly() throws SQLException {
            // before delete
            String query = "SELECT * FROM impression WHERE id=1;";
            Statement statement = conn.createStatement();
            ResultSet rs = statement.executeQuery(query);
            while(rs.next()) {
                assertEquals(1L, rs.getLong(1));
                assertEquals("path1", rs.getString(2));
                assertEquals(1L, rs.getLong(3));
                assertEquals(1, rs.getInt(4));
            }
            impressionRepository.delete(1L);

            // after delete
            rs = statement.executeQuery(query);
            assertFalse(rs.next());
        }
    }

    @Test
    void testFind() {
        assertEquals(new Impression(1L, "path1", 1L, 1), impressionRepository.find(1L));
    }

    @Test
    void testFindAll() throws SQLException {
        List<Impression> expected = new LinkedList<>();
        String query = "SELECT * FROM impression;";
        Statement statement = conn.createStatement();
        ResultSet rs = statement.executeQuery(query);
        while(rs.next()) {
            expected.add(new Impression(
                rs.getLong(1),
                rs.getString(2),
                rs.getLong(3), 
                rs.getInt(4)
            ));
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
            String query = "SELECT * FROM impression WHERE id=?";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setLong(1, 3L);
            ResultSet rs = statement.executeQuery();
            while(rs.next()) {
                assertEquals(3L, rs.getLong(1));
                assertEquals("path3", rs.getString(2));
                assertEquals(3L, rs.getLong(3));
                assertEquals(3, rs.getInt(4));
            }
        }
    }

    @Nested
    @DisplayName("Test update method")
    class TestUpdate {
        @Test
        void testUpdateReturnTrue() {
            assertTrue(impressionRepository.update(
                new Impression(2L, "path2", 2L, 2)));
        }

        @Test
        void testIfUpdateCorrectly() throws SQLException {
            Impression impression = null;

            // update
            String updatedPath = "path2_updated";
            Long updatedMarbleId = 123L;
            int updatedType = 123;

            impression = new Impression(
                2L,
                updatedPath,
                updatedMarbleId,
                updatedType
            );


            impressionRepository.update(impression);

            // after update
            Statement statement = conn.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM impression WHERE id=2;");
            while(rs.next()) {
                assertEquals(rs.getString(2), updatedPath);
                assertEquals(rs.getLong(3), updatedMarbleId);
                assertEquals(rs.getInt(4), updatedType);
            }

        }
    }

    @Test
    void testGetImpressionsByMarbleId() {
        List<Impression> expected = new LinkedList<>();
        Impression impression = new Impression(1L, "path1", 1L, 1);
        expected.add(impression);
        assertEquals(expected, impressionRepository.getImpressionsByMarbleId(1L));
    }

}
