package com.nayarasanchez.gestor_alojamientos.service;

import java.io.IOException;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;

import io.github.cdimascio.dotenv.Dotenv;

@Service
public class S3Service {

    private final AmazonS3 amazonS3;
    private final String bucketName;

    public S3Service(AmazonS3 amazonS3) {
        this.amazonS3 = amazonS3;
        Dotenv dotenv = Dotenv.load(); // carga el .env
        this.bucketName = dotenv.get("AWS_BUCKET");
    }

    // Upload file to S3 bucket
    public String uploadFile(MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        amazonS3.putObject(bucketName, fileName, file.getInputStream(), metadata);
        return amazonS3.getUrl(bucketName, fileName).toString(); // devuelve URL pública
    }

    public S3Object downloadFile(String fileName) {
        return amazonS3.getObject(bucketName, fileName);
    }

    /**
     * Elimina un archivo de S3 a partir de su URL.
     */
    public void deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isBlank()) return;

        String key = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);

        try {
            amazonS3.deleteObject(bucketName, key);
        } catch (Exception e) {
            System.err.println("Error eliminando archivo de S3: " + e.getMessage());
        }
    }

}