package com.example.demo.util.aws;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface AwsS3Service {
    String uploadFile(MultipartFile image, String folder, String filename) throws IOException;
    void deleteFile(String key);
}
