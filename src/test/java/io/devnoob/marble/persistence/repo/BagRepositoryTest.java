package io.devnoob.marble.persistence.repo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import io.devnoob.marble.persistence.entity.Bag;

public class BagRepositoryTest {

    private Connection conn;
    private String dbPath = "testDB.db";
    private BagRepository bagRepository;

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

        bagRepository = new BagRepository();
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

    @Nested
    @DisplayName("Test delete method")
    class TestDelete {
        @Test
        void testDeleteReturnTrue() {
            assertTrue(bagRepository.delete(1L));
            assertTrue(bagRepository.delete(2L));
        }

        @Test
        void testDeleteReturnFalse() {
            assertFalse(bagRepository.delete(3L));
        }

        @Test
        void testIfDeleteCorrectly() throws SQLException {
            // before delete
            String query = "SELECT * FROM bag WHERE id=1;";
            Statement statement = conn.createStatement();
            ResultSet rs = statement.executeQuery(query);
            while(rs.next()) {
                assertEquals(1L, rs.getLong(1));
                assertEquals("test_bag1", rs.getString(3));
            }
            bagRepository.delete(1L);

            // after delete
            rs = statement.executeQuery(query);
            assertFalse(rs.next());
        }
    }

    @Test
    void testFind() {
        assertEquals(new Bag(1L, 1L, "test_bag1", new Timestamp(1623917398)), bagRepository.find(1L));
    }

    @Test
    void testFindAll() throws SQLException {
        List<Bag> expected = new LinkedList<>();
        String query = "SELECT * FROM bag;";
        Statement statement = conn.createStatement();
        ResultSet rs = statement.executeQuery(query);;
        while(rs.next()) {
            expected.add(new Bag(rs.getLong(1), rs.getLong(2), rs.getString(3), rs.getTimestamp(4)));
        }
        assertEquals(expected, bagRepository.findAll());
    }


    @Nested
    @DisplayName("Test insert method")
    class TestInsert {
        @Test
        void testInsertReturnTrue() {
            assertTrue(bagRepository.insert(new Bag(1L,"test_bag3",new Timestamp(1623917398))));
        }

        @Test
        void testInsertCorrectly() throws SQLException {
            bagRepository.insert(new Bag(1L,"test_bag3",new Timestamp(1623917398)));
            String query = "SELECT name FROM bag WHERE name=?";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, "test_bag3");
            ResultSet rs = statement.executeQuery();
            while(rs.next()) {
                assertEquals("test_bag3", rs.getString(1));
            }
        }
    }

    @Nested
    @DisplayName("Test update method")
    class TestUpdate {
        @Test
        void testUpdateReturnTrue() {
            assertTrue(bagRepository.update(
                new Bag(2L, 123L, "bag_name_updated", new Timestamp(12312312))));
        }

        @Test
        void testIfUpdateCorrectly() throws SQLException {
            Bag bag = null;

            // update
            Long updatedUserId = 123L;
            String updatedName = "bag_name_updated";
            Timestamp updatedCreationTime = new Timestamp(12312312);

            bag = new Bag(
                2L,
                updatedUserId,
                updatedName,
                updatedCreationTime
            );


            bagRepository.update(bag);

            // after update
            Statement statement = conn.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM bag WHERE id=2;");
            while(rs.next()) {
                assertEquals(rs.getLong(2), updatedUserId);
                assertEquals(rs.getString(3), updatedName);
                assertEquals(rs.getTimestamp(4), updatedCreationTime);
            }
        }
    }

    @Test
    void testGetBagsByUserId() {
        List<Bag> expected = new LinkedList<>();
        Bag bag = new Bag(1L, 1L, "test_bag1", new Timestamp(1623917398));
        expected.add(bag);
        assertEquals(expected, bagRepository.getBagsByUserId(1L));
    }
}
