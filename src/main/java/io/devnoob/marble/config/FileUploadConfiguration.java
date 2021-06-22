package io.devnoob.marble.config;

import java.io.File;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class FileUploadConfiguration {

    final public static int VEDIO_TYPE = 1;
    final public static int IMAGE_TYPE = 2;
    final public static int AUDIO_TYPE = 3;

    @Value("${upload.dir}")
    private String uploadDirPath;

    @Value("${upload.path}")
    private String uploadPath;

    public String getUploadDirPath() {
        return uploadDirPath;
    }

    public String getUploadPath() {
        return uploadPath;
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

    @Bean
    WebMvcConfigurer webMvcConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addResourceHandlers(ResourceHandlerRegistry registry) {
                registry.addResourceHandler("/"+uploadPath+"**")
                .addResourceLocations("file:"+uploadDirPath);
            }
        };
    }
}
