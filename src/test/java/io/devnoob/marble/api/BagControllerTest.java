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
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import io.devnoob.marble.persistence.entity.Bag;
import io.devnoob.marble.persistence.entity.Marble;
import io.devnoob.marble.persistence.repo.BagRepository;
import io.devnoob.marble.persistence.repo.MarbleBagRepository;

@WebMvcTest(BagController.class)
public class BagControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BagRepository bagRepository;

    @MockBean
    private MarbleBagRepository marbleBagRepository;

    @Test
    void testBatchRemove() throws Exception {
        List<Long> ids = new LinkedList<>();
        ids.add(1L);
        ids.add(2L);

        Mockito.when(bagRepository.delete(1L)).thenReturn(true);
        Mockito.when(bagRepository.delete(2L)).thenReturn(true);

        String url = "/api/bag/batchremove/";

        mockMvc.perform(
            delete(url)
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(ids))
            ).andExpect(status().isOk())
            .andExpect(content().string("true"));
            
            Mockito.verify(bagRepository, times(1)).delete(1L);
            Mockito.verify(bagRepository, times(1)).delete(2L);
    }

    @Test
    void testCreatBag() throws Exception {
        Bag newBag = new Bag(1L, "test_bag1", new Timestamp(1623917399));
        Mockito.when(bagRepository.insert(newBag)).thenReturn(true);

        String url = "/api/bag/";
        mockMvc.perform(
            post(url)
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(newBag))
        ).andExpect(status().isOk())
        .andExpect(content().string("true"));
        Mockito.verify(bagRepository, times(1)).insert(newBag);
    }

    @Test
    void testDeleteBag() throws Exception {
        Mockito.when(bagRepository.delete(1L)).thenReturn(true);
        String url = "/api/bag/1";
        mockMvc.perform(delete(url))
            .andExpect(status().isOk())
            .andExpect(content().string("true"));
        Mockito.verify(bagRepository, times(1)).delete(1L);
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
            Mockito.verify(bagRepository, times(1)).getBagsByUserId(1L);
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
            Mockito.verify(bagRepository, times(1)).getBagsByUserId(1L);
        }
    }


    @Test
    void testUpdateBag() throws Exception {
        Bag updatedBag = new Bag(1L, 1L, "test_bag1", new Timestamp(1623917398));
        Mockito.when(bagRepository.update(updatedBag)).thenReturn(true);

        String url = "/api/bag/";
        mockMvc.perform(
            put(url)
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(updatedBag))
        ).andExpect(status().isOk())
        .andExpect(content().string("true"));

        Mockito.verify(bagRepository, times(1)).update(updatedBag);
    }

    @Nested
    @DisplayName("Test get marbles by given bag id API")
    class TestGetMarbleByBagId {
        @Test
        @DisplayName("Should return 200 OK if bag id is valid")
        void shouldReturn200OKIfBagIdIsValid() throws Exception {
            Bag bag = new Bag(1L, 1L, "test_bag1", new Timestamp(1623917398));
            Mockito.when(bagRepository.find(1L)).thenReturn(bag);
            Marble marble = new Marble(
                1L, "test_marble1", 1L, new Timestamp(1623917420), "marble1_test", "story_marble1");
            List<Marble> marbles = new LinkedList<>();
            marbles.add(marble);
            Mockito.when(marbleBagRepository.getMarbleByBagId(1L)).thenReturn(marbles);

            String url = "/api/bag/marbles/1";
            mockMvc.perform(get(url)).andExpect(status().isOk());
            Mockito.verify(bagRepository, times(1)).find(1L);
            Mockito.verify(marbleBagRepository, times(1)).getMarbleByBagId(1L);
        }

        @Test
        @DisplayName("Should return 404 not found if bag id is not valid")
        void shouldReturn404NotFoundIfBagIdIsNotValid() throws Exception {
            Mockito.when(bagRepository.find(100L)).thenReturn(null);
            Marble marble = new Marble(
                1L, "test_marble1", 1L, new Timestamp(1623917420), "marble1_test", "story_marble1");
            List<Marble> marbles = new LinkedList<>();
            marbles.add(marble);
            Mockito.when(marbleBagRepository.getMarbleByBagId(100L)).thenReturn(marbles);

            String url = "/api/bag/marbles/100";
            mockMvc.perform(get(url)).andExpect(status().isNotFound());
            Mockito.verify(bagRepository, times(1)).find(100L);
            Mockito.verify(marbleBagRepository, times(0)).getMarbleByBagId(100L);
        }

        @Test
        @DisplayName("Should return correct marble list")
        void shouldReturnCorrectMarbles() throws Exception {
            Bag bag = new Bag(1L, 1L, "test_bag1", new Timestamp(1623917398));
            Mockito.when(bagRepository.find(1L)).thenReturn(bag);
            Marble marble = new Marble(
                1L, "test_marble1", 1L, new Timestamp(1623917420), "marble1_test", "story_marble1");
            List<Marble> marbles = new LinkedList<>();
            marbles.add(marble);
            Mockito.when(marbleBagRepository.getMarbleByBagId(1L)).thenReturn(marbles);

            String url = "/api/bag/marbles/1";
            MvcResult mvcResult = mockMvc.perform(get(url)).andReturn();
            String actualJsonResponse = mvcResult.getResponse().getContentAsString();
            String expectedJsonResponse = objectMapper.writeValueAsString(marbles);
            assertEquals(expectedJsonResponse, actualJsonResponse); 
            
            Mockito.verify(bagRepository, times(1)).find(1L);
            Mockito.verify(marbleBagRepository, times(1)).getMarbleByBagId(1L);
        }
    }
}
