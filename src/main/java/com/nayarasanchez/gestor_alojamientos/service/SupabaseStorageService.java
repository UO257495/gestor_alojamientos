package com.nayarasanchez.gestor_alojamientos.service;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class SupabaseStorageService {

    private final String supabaseUrl;
    private final String supabaseKey;
    private final String bucketName;
    private final RestTemplate restTemplate;

    public SupabaseStorageService() {
        Dotenv dotenv = Dotenv.load();
        this.supabaseUrl = dotenv.get("SUPABASE_URL");
        this.supabaseKey = dotenv.get("SUPABASE_KEY");
        this.bucketName = dotenv.get("SUPABASE_BUCKET"); 
        this.restTemplate = new RestTemplate();
    }

    public String uploadFile(MultipartFile file) throws IOException {
        String originalName = file.getOriginalFilename() != null
                ? file.getOriginalFilename().replace(" ", "_")
                : "imagen.jpg";

        String fileName = System.currentTimeMillis() + "_" + originalName;

        String url = supabaseUrl + "/storage/v1/object/" + bucketName + "/" + fileName;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(supabaseKey);
        headers.set("apikey", supabaseKey);
        headers.set("x-upsert", "true");

        String contentType = file.getContentType();
        headers.setContentType(contentType != null
                ? MediaType.parseMediaType(contentType)
                : MediaType.APPLICATION_OCTET_STREAM);

        HttpEntity<byte[]> entity = new HttpEntity<>(file.getBytes(), headers);

        try {
            restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);

            return supabaseUrl + "/storage/v1/object/public/" + bucketName + "/" + fileName;

        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("Error al subir a Supabase: " + e.getMessage(), e);
        }
    }

    public byte[] downloadFile(String fileName) {
        String url = supabaseUrl + "/storage/v1/object/" + bucketName + "/" + fileName;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + supabaseKey);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<byte[]> response = restTemplate.exchange(url, HttpMethod.GET, entity, byte[].class);
        return response.getBody();
    }

    public void deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isBlank()) return;

        String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
        String url = supabaseUrl + "/storage/v1/object/" + bucketName + "/" + fileName;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + supabaseKey);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            restTemplate.exchange(url, HttpMethod.DELETE, entity, String.class);
        } catch (Exception e) {
            System.err.println("Error eliminando en Supabase: " + e.getMessage());
        }
    }
}