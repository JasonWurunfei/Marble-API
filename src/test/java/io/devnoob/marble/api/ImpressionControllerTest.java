package io.devnoob.marble.api;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.LinkedList;
import java.util.List;

import io.devnoob.marble.persistence.entity.Impression;
import io.devnoob.marble.persistence.repo.ImpressionRepository;

@SpringBootTest
@AutoConfigureMockMvc
public class ImpressionControllerTest {

    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ImpressionRepository impressionRepository;
    
    @Test
    void testBatchRemove() throws Exception {
        List<Long> ids = new LinkedList<>();
        ids.add(1L);
        ids.add(2L);

        Mockito.when(impressionRepository.delete(1L)).thenReturn(true);
        Mockito.when(impressionRepository.delete(2L)).thenReturn(true);

        String url = "/api/impression/batchremove/";

        mockMvc.perform(
            delete(url)
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(ids))
            ).andExpect(status().isOk())
            .andExpect(content().string("true"));
            
        Mockito.verify(impressionRepository, times(1)).delete(1L);
        Mockito.verify(impressionRepository, times(1)).delete(2L);
    }

    @Test
    void testCreateImpression() throws Exception{
        Mockito.when(impressionRepository.insert(any(Impression.class))).thenReturn(true);

        String url = "/api/impression/";
        
        MockMultipartFile testFile = new MockMultipartFile("file", "filename.txt", "text/plain", "some xml".getBytes());
        mockMvc.perform(MockMvcRequestBuilders.multipart(url)
            .file(testFile)
            .param("marbleId", "1")
            .param("type", "2")
        ).andExpect(status().isOk())
        .andExpect(content().string("true"));
    }

    @Test
    void testDeleteImpression() throws Exception {
        Mockito.when(impressionRepository.delete(1L)).thenReturn(true);

        String url = "/api/impression/1";
        mockMvc.perform(delete(url))
            .andExpect(status().isOk())
            .andExpect(content().string("true"));
        Mockito.verify(impressionRepository, times(1)).delete(1L);;
    }

    @Nested
    @DisplayName("Test get impressions by given marble id API")
    class testGetimpressionsByMarbleId {

        @Test
        @DisplayName("Should return OK when marble exists")
        void getImpressionsByMarbleIdShouldReturnOkWhenMarbleIdExist() throws Exception {
            List<Impression> impressions = new LinkedList<>();
            impressions.add(new Impression(1L, "path1", 1L, 3));
            Mockito.when(impressionRepository.getImpressionsByMarbleId(1L))
                .thenReturn(impressions);

            String url = "/api/impression/marble/1";
            mockMvc.perform(get(url)).andExpect(status().isOk());
            Mockito.verify(impressionRepository, times(1)).getImpressionsByMarbleId(1L);
        }

        @Test
        @DisplayName("Get impressions shoud return correct response body")
        void getImpressionsByMarbleIdReturnCorrectResponseBody() throws Exception {
            List<Impression> impressions = new LinkedList<>();
            impressions.add(new Impression(1L, "path1", 1L, 3));
            Mockito.when(impressionRepository.getImpressionsByMarbleId(1L)).thenReturn(impressions);

            String url = "/api/impression/marble/1";
            MvcResult mvcResult = mockMvc.perform(get(url)).andReturn();
            String actualJsonResponse = mvcResult.getResponse().getContentAsString();
            String expectedJsonResponse = objectMapper.writeValueAsString(impressions);
            assertEquals(expectedJsonResponse, actualJsonResponse); 
            Mockito.verify(impressionRepository, times(1)).getImpressionsByMarbleId(1L);
        }
    }
}
