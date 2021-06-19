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
    void testBatchRemove() {

    }

    @Test
    void testCreateUser() {

    }

    @Test
    void testDeleteMarble() {

    }

    @Test
    void testGetMarble() {

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
        }


        @Test
        @DisplayName("Should return 404 when marble does not exist")
        void getUserShouldReturn404WhenMarbleDoesNotExist() throws Exception {
            Mockito.when(marbleRepository.find(2L)).thenReturn(null);
    
            String url = "/api/marble/2";
            mockMvc.perform(get(url)).andExpect(status().isNotFound());
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
        }
    }

    @Test
    void testUpdateMarble() {

    }
}
