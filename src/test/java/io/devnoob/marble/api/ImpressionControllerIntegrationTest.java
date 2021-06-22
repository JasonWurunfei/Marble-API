package io.devnoob.marble.api;

import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import io.devnoob.marble.persistence.entity.Impression;
import io.devnoob.marble.persistence.repo.ImpressionRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ImpressionControllerIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ImpressionRepository impressionRepository;

    private Connection conn;
    private String dbPath = "testDB.db";
    private static String uploadDirPath = "test-upload";

    @BeforeEach
    void setUp() throws SQLException {
        conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
        String query = "CREATE TABLE IF NOT EXISTS impression (" + 
                        "id         integer PRIMARY KEY," + 
                        "path       text NOT NULL," + 
                        "marble_id  integer NOT NULL," +
                        "type       integer NOT Null" + 
                 ");";
        Statement statement = conn.createStatement();
        statement.execute(query);

        String query1 = "INSERT INTO impression(path, marble_id, type) VALUES(\"path1\", 1, 1);";
        String query2 = "INSERT INTO impression(path, marble_id, type) VALUES(\"path2\", 2, 3);";
        
        statement.executeUpdate(query1);
        statement.executeUpdate(query2);
        statement.close();
        
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

    static void deleteDirectoryRecursion(File file) throws IOException {
        if (file.isDirectory()) {
          File[] entries = file.listFiles();
          if (entries != null) {
            for (File entry : entries) {
                deleteDirectoryRecursion(entry);
            }
          }
        }
        if (!file.delete()) {
          throw new IOException("Failed to delete " + file);
        }
      }

    @AfterAll
    public static void removeTestDir() {
        File uploadDir = new File(uploadDirPath);
        if (uploadDir.exists()) {
            try {
                deleteDirectoryRecursion(uploadDir);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    void testCreateImpression() throws Exception{
        String url = "/api/impression/";
        
        MockMultipartFile testFile = new MockMultipartFile("file", "filename.txt", "text/plain", "some xml".getBytes());
        mockMvc.perform(MockMvcRequestBuilders.multipart(url)
            .file(testFile)
            .param("marbleId", "1")
            .param("type", "2")
        ).andExpect(status().isOk())
        .andExpect(content().string("true"));

        String query = "SELECT * FROM impression WHERE id=?;";
        PreparedStatement statement = conn.prepareStatement(query);
        statement.setLong(1, 3L);
        ResultSet rs = statement.executeQuery();
        while(rs.next()) {
            assertEquals(1, rs.getLong(3));
            assertEquals(2, rs.getInt(4));
        }
    }

    @Test
    void testDeleteImpression() throws Exception {
        String url = "/api/impression/1";
        mockMvc.perform(delete(url))
            .andExpect(status().isOk())
            .andExpect(content().string("true"));

        String query = "SELECT * FROM impression WHERE id=1;";
        Statement statement = conn.createStatement();
        ResultSet rs = statement.executeQuery(query);
        assertFalse(rs.next());
    }

    @Nested
    @DisplayName("Test get impressions by given marble_id API")
    class TestGetImpressionsByMarbleId {

        @Test
        @DisplayName("Should return OK when marble exists")
        void getImpressionsByMarbleIdShouldReturnOkWhenMarbleIdExist() throws Exception {
            String url = "/api/impression/marble/1";
            mockMvc.perform(get(url)).andExpect(status().isOk());
        }

        @Test
        @DisplayName("Get impressions shoud return correct response body")
        void getImpressionsByMarbleIdReturnCorrectResponseBody() throws Exception {
            List<Impression> impressions = new LinkedList<>();
            impressions.add(new Impression(1L, "path1", 1L, 1));

            String url = "/api/impression/marble/1";
            MvcResult mvcResult = mockMvc.perform(get(url)).andReturn();
            String actualJsonResponse = mvcResult.getResponse().getContentAsString();
            String expectedJsonResponse = objectMapper.writeValueAsString(impressions);
            assertEquals(expectedJsonResponse, actualJsonResponse); 
        }
    }

    @Test
    void testImpressionBatchRemove() throws Exception {
        List<Long> ids = new LinkedList<>();
        ids.add(1L);
        ids.add(2L);
        String url = "/api/impression/batchremove/";
        mockMvc.perform(
            delete(url)
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(ids))
            ).andExpect(status().isOk())
            .andExpect(content().string("true"));

        String query1 = "DELETE FROM impression WHERE id IN (1,2);";
        Statement statement1 = conn.createStatement();
        statement1.executeUpdate(query1);
        
        String query2 = "SELECT * FROM impression WHERE id=1 or id=2;";
        Statement statement2 = conn.createStatement();
        ResultSet rs = statement2.executeQuery(query2);
        assertFalse(rs.next());
    }
}
