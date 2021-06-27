package io.devnoob.marble.persistence.repo;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.devnoob.marble.persistence.entity.Marble;
import io.devnoob.marble.persistence.entity.MarbleBag;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;

public class MarbleBagRepositoryTest {

    private Connection conn;
    private String dbPath = "testDB.db";
    private MarbleBagRepository marbleBagRepository;

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
        
        marbleBagRepository = new MarbleBagRepository();
        marbleBagRepository.setDbPath(dbPath);
        marbleBagRepository.connect();
    }

    @AfterEach
    void cleanUp() throws SQLException {
        conn.close();
        marbleBagRepository.close();
        File db = new File(dbPath);
        if (db.exists()) {
            db.delete();
        }
    }

    @Test
    void testGetMarbleByBagId() {
        Marble marble1 = new Marble(1L, "test_marble1", 1L, new Timestamp(1623917398), "marble1_test", "story_marble1");
        Marble marble2 = new Marble(2L, "test_marble2", 2L, new Timestamp(1623917480), "marble2_test", "story_marble2");
        Marble marble3 = new Marble(3L, "test_marble3", 2L, new Timestamp(1623912280), "marble3_test", "story_marble3");
        List<Marble> expected = new LinkedList<>();
        expected.add(marble1);
        expected.add(marble3);
        List<Marble> actual = marbleBagRepository.getMarbleByBagId(2L);
        assertEquals(expected, actual);

        expected = new LinkedList<>();
        expected.add(marble1);
        expected.add(marble2);
        expected.add(marble3);
        actual = marbleBagRepository.getMarbleByBagId(1L);
        assertEquals(expected, actual);
    }

    @Test
    void testFind() {
        MarbleBag expected = new MarbleBag(2L, 2L, 1L);
        MarbleBag actual = marbleBagRepository.find(2L);
        assertEquals(expected, actual);

        expected = new MarbleBag(4L, 3L, 2L);
        actual = marbleBagRepository.find(4L);
        assertEquals(expected, actual);
    }
}
