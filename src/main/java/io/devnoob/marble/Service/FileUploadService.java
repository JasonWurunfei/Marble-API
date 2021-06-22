package io.devnoob.marble.Service;

import java.io.File;
import java.io.IOException;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import io.devnoob.marble.config.FileUploadConfiguration;
import static io.devnoob.marble.config.FileUploadConfiguration.*;

@Service
public class FileUploadService {

    @Autowired
    FileUploadConfiguration FileUploadConfiguration;

    public String uploadFile(MultipartFile file, Long marbleId, int type) {
        String filePath = String.format("%s-%d-%d.%s",
            marbleId.toString(), 
            type,
            System.currentTimeMillis(),
            getExtension(file.getOriginalFilename())
        );
        
        switch(type) {
            case VEDIO_TYPE:
                filePath = "video/" + filePath;
                break;
            case IMAGE_TYPE:
                filePath = "image/" + filePath;
                break;
            case AUDIO_TYPE:
                filePath = "audio/" + filePath;
                break;
        }

        File dest = new File(FileUploadConfiguration.getUploadDirPath() + filePath);
        try {
            file.transferTo(dest);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return getURL(filePath);
    }

    public String getExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".")+1);
    }

    private String getURL(String filePath) {
        return FileUploadConfiguration.getUploadPath()+filePath;
    }

}
