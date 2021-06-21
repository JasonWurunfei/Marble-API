package io.devnoob.marble.config;

import java.io.File;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FileUploadConfiguration {

    final public static int VEDIO_TYPE = 1;
    final public static int IMAGE_TYPE = 2;
    final public static int AUDIO_TYPE = 3;

    @Value("${upload.dir}")
    private String uploadDirPath;

    public String getUploadDirPath() {
        return uploadDirPath;
    }

    @Bean
    public void setUpUploadDirs() {
        File uploadDir = new File(uploadDirPath);
        if (uploadDir.exists() == false) {
            setUp(uploadDir);
        }
    }

    private void setUp(File uploadDir) {
        uploadDir.mkdirs();
        new File(uploadDir.getAbsolutePath(), "video").mkdir();
        new File(uploadDir.getAbsolutePath(), "image").mkdir();
        new File(uploadDir.getAbsolutePath(), "audio").mkdir();
    }
}
