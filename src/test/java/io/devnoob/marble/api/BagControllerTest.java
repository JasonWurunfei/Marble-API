package io.devnoob.marble.api;

import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;

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

import io.devnoob.marble.persistence.entity.Bag;
import io.devnoob.marble.persistence.repo.BagRepository;

@WebMvcTest(BagController.class)
public class BagControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BagRepository bagRepository;

    @Test
    void testBatchRemove() {

    }

    @Test
    void testCreateUser() {

    }

    @Test
    void testDeleteMarble() {

    }

    @Nested
    @DisplayName("Test GetBagAPI")
    class testGetBagAPI {

        @Test
        @DisplayName("Should return ok when bag exists")
        void getBagShouldReturnOkWhenBagExist() throws Exception {
            Mockito.when(bagRepository.find(1L))
                .thenReturn(new Bag(1L, 1L, "test_bag1", new Timestamp(1623917398)));
    
            String url = "/api/bag/marble/1";
            mockMvc.perform(get(url)).andExpect(status().isOk());
        }


        @Test
        @DisplayName("Should return 404 when user does not exist")
        void getBagShouldReturn404WhenBagDoesNotExist() throws Exception {
            Mockito.when(bagRepository.find(2L)).thenReturn(null);
    
            String url = "/api/bag/marble/2";
            mockMvc.perform(get(url)).andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("GetBag shoud return correct response body")
        void getUserReturnCorrectResponseBody() throws Exception {
            Bag bag = new Bag(1L, "test_bag1", new Timestamp(1623917398));
            Mockito.when(bagRepository.find(1L))
                .thenReturn(bag);
    
            String url = "/api/bag/marble/1";
            MvcResult mvcResult = mockMvc.perform(get(url)).andReturn();
            String actualJsonResponse = mvcResult.getResponse().getContentAsString();
            String expectedJsonResponse = objectMapper.writeValueAsString(bag);
            assertEquals(expectedJsonResponse, actualJsonResponse);
        }
    }

    @Nested
    @DisplayName("Test get bags by given user_id API")
    class TestGetBagsByUserId {

        @Test
        @DisplayName("Should return OK when user exist")
        void getBagsByUserIdShouldReturnOkWhenUserExist() throws Exception {
            List<Bag> bags = new LinkedList<>();
            bags.add(new Bag(1L, 1L, "test_bag1", new Timestamp(1623917398)));
            Mockito.when(bagRepository.getBagsByUserId(1L))
            .thenReturn(bags);

            String url = "/api/bag/user/1";
            mockMvc.perform(get(url)).andExpect(status().isOk());
        }

        @Test
        @DisplayName("Get bags shoud return correct response body")
        void getBagsByUserIdReturnCorrectResponseBody() throws Exception {
            List<Bag> bags = new LinkedList<>();
            bags.add(new Bag(1L, 1L, "test_bag1", new Timestamp(1623917398)));
            Mockito.when(bagRepository.getBagsByUserId(1L)).thenReturn(bags);

            String url = "/api/bag/user/1";
            MvcResult mvcResult = mockMvc.perform(get(url)).andReturn();
            String actualJsonResponse = mvcResult.getResponse().getContentAsString();
            String expectedJsonResponse = objectMapper.writeValueAsString(bags);
            assertEquals(expectedJsonResponse, actualJsonResponse); 
        }
    }


    @Test
    void testUpdateMarble() {

    }
}
