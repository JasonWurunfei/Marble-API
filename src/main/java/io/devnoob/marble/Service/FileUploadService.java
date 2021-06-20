package io.devnoob.marble.Service;

import java.io.File;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileUploadService {

    @Value("${upload.dir}")
    private String uploadDir;

    public String getUploadDir() {
        return uploadDir;
    }

    public void setUploadDir(String uploadDir) {
        this.uploadDir = uploadDir;
    }

    public String uploadFile(MultipartFile file, Long marbleId, int type) {
        String filePath = String.format("%s-%d-%d.%s",
            marbleId.toString(), 
            type,
            System.currentTimeMillis(),
            getExtension(file.getOriginalFilename())
        );
        filePath = this.uploadDir + filePath;
        File dest = new File(filePath);
        try {
            file.transferTo(dest);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return filePath;
    }

    public String getExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".")+1);
    }

}
