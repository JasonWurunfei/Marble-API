package io.devnoob.marble.Service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;

@SpringBootTest
@ActiveProfiles("test")
public class FileUploadServiceTest {

    @Autowired
    private FileUploadService fileUploadService;

    @MockBean
    private MultipartFile multipartFile;

    @Test
    void testGetExtention() {
        String expectedExtension = "mp4";
        Mockito.when(multipartFile.getOriginalFilename()).thenReturn("Hello." + expectedExtension);
        String actualExtension = fileUploadService.getExtension(multipartFile.getOriginalFilename());
        assertEquals(expectedExtension, actualExtension);
    }
}
