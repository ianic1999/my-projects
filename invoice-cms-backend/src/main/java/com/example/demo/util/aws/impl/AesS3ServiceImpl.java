package com.example.demo.util.aws.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.demo.util.aws.AwsS3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class AesS3ServiceImpl implements AwsS3Service {
    private final AmazonS3 amazonS3;
    @Value("${aws.s3.bucket}")
    private String bucketName;

    @Override
    public String uploadFile(MultipartFile image, String folder, String filename) throws IOException {
        File file = convertMultiPartFileToFile(image);
        String key = folder + filename;
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, file);
        amazonS3.putObject(putObjectRequest);
        file.delete();
        return filename;
    }

    @Override
    public void deleteFile(String key) {
        amazonS3.deleteObject(bucketName, key);
    }

    private File convertMultiPartFileToFile(final MultipartFile multipartFile) throws IOException {
        final File file = new File(multipartFile.getOriginalFilename());
        final FileOutputStream outputStream = new FileOutputStream(file);
        outputStream.write(multipartFile.getBytes());
        outputStream.close();
        return file;
    }


}
