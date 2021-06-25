package io.devnoob.marble.api;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;

import io.devnoob.marble.persistence.entity.Marble;
import io.devnoob.marble.persistence.repo.MarbleRepository;

@WebMvcTest(MarbleController.class)
public class MarbleControllerTest {
    
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MarbleRepository marbleRepository;


    @Test
    void testBatchRemove() throws Exception {
        List<Long> ids = new LinkedList<>();
        ids.add(1L);
        ids.add(2L);

        Mockito.when(marbleRepository.delete(1L)).thenReturn(true);
        Mockito.when(marbleRepository.delete(2L)).thenReturn(true);

        String url = "/api/marble/batchremove/";

        mockMvc.perform(
            delete(url)
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(ids))
            ).andExpect(status().isOk())
            .andExpect(content().string("true"));
            
            Mockito.verify(marbleRepository, times(1)).delete(1L);
            Mockito.verify(marbleRepository, times(1)).delete(2L);
    }

    @Test
    void testCreateMarble() throws Exception {
        Marble newMarble = new Marble("test_marble1", 1L, new Timestamp(1623917398), "marble1_test", "story_marble1");
        Mockito.when(marbleRepository.insert(newMarble)).thenReturn(true);

        String url = "/api/marble/";
        mockMvc.perform(
            post(url)
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(newMarble))
        ).andExpect(status().isOk())
        .andExpect(content().string("true"));
        Mockito.verify(marbleRepository, times(1)).insert(newMarble);
    }

    @Test
    void testDeleteMarble() throws Exception {
        Mockito.when(marbleRepository.delete(1L)).thenReturn(true);
        String url = "/api/marble/1";
        mockMvc.perform(delete(url))
            .andExpect(status().isOk())
            .andExpect(content().string("true"));
        Mockito.verify(marbleRepository, times(1)).delete(1L);
    }

    @Nested
    @DisplayName("Test GetMarbleAPI")
    class testGetMarbleAPI {

        @Test
        @DisplayName("Should return ok when marble exists")
        void getMarbleShouldReturnOkWhenMarbleExist() throws Exception {
            Mockito.when(marbleRepository.find(1L))
                .thenReturn(new Marble(1L, "test_marble1", 1L, new Timestamp(1623917398), "marble1_test", "story_marble1"));
    
            String url = "/api/marble/1";
            mockMvc.perform(get(url)).andExpect(status().isOk());
            Mockito.verify(marbleRepository, times(1)).find(1L);
        }


        @Test
        @DisplayName("Should return 404 when marble does not exist")
        void getUserShouldReturn404WhenMarbleDoesNotExist() throws Exception {
            Mockito.when(marbleRepository.find(2L)).thenReturn(null);
    
            String url = "/api/marble/2";
            mockMvc.perform(get(url)).andExpect(status().isNotFound());
            Mockito.verify(marbleRepository, times(1)).find(2L);
        }

        @Test
        @DisplayName("GetMarble shoud return correct response body")
        void getMarbleReturnCorrectResponseBody() throws Exception {
            Marble marble = new Marble(1L, "test_marble1", 1L, new Timestamp(1623917398), "marble1_test", "story_marble1");
            Mockito.when(marbleRepository.find(1L))
                .thenReturn(marble);
    
            String url = "/api/marble/1";
            MvcResult mvcResult = mockMvc.perform(get(url)).andReturn();
            String actualJsonResponse = mvcResult.getResponse().getContentAsString();
            String expectedJsonResponse = objectMapper.writeValueAsString(marble);
            assertEquals(expectedJsonResponse, actualJsonResponse);
            Mockito.verify(marbleRepository, times(1)).find(1L);
        }

    }

    @Nested
    @DisplayName("Test get marbles by given user id API")
    class testGetMarblesByUserId {

        @Test
        @DisplayName("Should return OK when user exist")
        void getMarblesByUserIdShouldReturnOkWhenUserExist() throws Exception {
            List<Marble> marbles = new LinkedList<>();
            marbles.add(new Marble(1L, "test_marble1", 1L, new Timestamp(1623917398), "marble1_test", "story_marble1"));
            Mockito.when(marbleRepository.getMarblesByUserId(1L))
                .thenReturn(marbles);

            String url = "/api/marble/user/1";
            mockMvc.perform(get(url)).andExpect(status().isOk());
            Mockito.verify(marbleRepository, times(1)).getMarblesByUserId(1L);
        }

        @Test
        @DisplayName("Get marbles shoud return correct response body")
        void getMarblesByUserIdReturnCorrectResponseBody() throws Exception {
            List<Marble> marbles = new LinkedList<>();
            marbles.add(new Marble(1L, "test_marble1", 1L, new Timestamp(1623917398), "marble1_test", "story_marble1"));
            Mockito.when(marbleRepository.getMarblesByUserId(1L)).thenReturn(marbles);

            String url = "/api/marble/user/1";
            MvcResult mvcResult = mockMvc.perform(get(url)).andReturn();
            String actualJsonResponse = mvcResult.getResponse().getContentAsString();
            String expectedJsonResponse = objectMapper.writeValueAsString(marbles);
            assertEquals(expectedJsonResponse, actualJsonResponse); 
            Mockito.verify(marbleRepository, times(1)).getMarblesByUserId(1L);
        }

        @Test
        @DisplayName("Should return OK and in the form of DESC when user exist")
        void getLatestMarblesByUserIdShouldReturnOkWhenUserExist() throws Exception {
            List<Marble> marbles = new LinkedList<>();
            marbles.add(new Marble(1L, "test_marble1", 1L, new Timestamp(1623917398), "marble1_test", "story_marble1"));
            Mockito.when(marbleRepository.getLatestMarblesByUserId(1L, 2))
                .thenReturn(marbles);

            String url = "/api/marble/latest/1?limit=2";
            mockMvc.perform(get(url)).andExpect(status().isOk());
            Mockito.verify(marbleRepository, times(1)).getLatestMarblesByUserId(1L, 2);
        }

        @Test
        @DisplayName("Should return OK and matchable number when user exist")
        void getLatestMarblesByUserIdShouldReturnOkAndMatchableNumberWhenUserExist() throws Exception {
            List<Marble> marbles = new LinkedList<>();
            marbles.add(new Marble(1L, "test_marble1", 1L, new Timestamp(1623917398), "marble1_test", "story_marble1"));
            marbles.add(new Marble(1L, "test_marble2", 1L, new Timestamp(1623917377), "marble2_test", "story_marble2"));
            Mockito.when(marbleRepository.getLatestMarblesByUserId(1L, 2))
                .thenReturn(marbles);

            String url = "/api/marble/latest/1?limit=2";
            MvcResult mvcResult = mockMvc.perform(get(url)).andReturn();
            String actualJsonResponse = mvcResult.getResponse().getContentAsString();
            String expectedJsonResponse = objectMapper.writeValueAsString(marbles);
            assertEquals(expectedJsonResponse, actualJsonResponse);
            Mockito.verify(marbleRepository, times(1)).getLatestMarblesByUserId(1L, 2);
        }

        @Test
        @DisplayName("Should return OK and matchable number when user exist")
        void getLatestMarblesByUserIdShouldReturnOkAndMatchableNumberWhenMore() throws Exception {
            List<Marble> marbles = new LinkedList<>();
            marbles.add(new Marble(1L, "test_marble1", 1L, new Timestamp(1623917398), "marble1_test", "story_marble1"));
            marbles.add(new Marble(1L, "test_marble2", 1L, new Timestamp(1623917377), "marble2_test", "story_marble2"));
            Mockito.when(marbleRepository.getLatestMarblesByUserId(1L, 100))
                .thenReturn(marbles);

            String url = "/api/marble/latest/1?limit=100";
            MvcResult mvcResult = mockMvc.perform(get(url)).andReturn();
            String actualJsonResponse = mvcResult.getResponse().getContentAsString();
            String expectedJsonResponse = objectMapper.writeValueAsString(marbles);
            assertEquals(expectedJsonResponse, actualJsonResponse);
            Mockito.verify(marbleRepository, times(1)).getLatestMarblesByUserId(1L, 100);
        }
    }

    @Test
    void testUpdateMarble() throws Exception {
        Marble updatedMarble = new Marble(1L, "test_marble1", 1L, new Timestamp(1623917398), "marble1_test", "story_marble1");
        Mockito.when(marbleRepository.update(updatedMarble)).thenReturn(true);

        String url = "/api/marble/";
        mockMvc.perform(
            put(url)
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(updatedMarble))
        ).andExpect(status().isOk())
        .andExpect(content().string("true"));

        Mockito.verify(marbleRepository, times(1)).update(updatedMarble);
    }
}
