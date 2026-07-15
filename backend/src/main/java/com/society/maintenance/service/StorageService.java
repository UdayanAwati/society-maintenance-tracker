package com.society.maintenance.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.society.maintenance.exception.ApiException;
import java.nio.file.*;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class StorageService {
    private static final Set<String> ALLOWED = Set.of("image/jpeg", "image/jpg", "image/png");
    private final Cloudinary cloudinary;
    private final String localDir;

    public StorageService(@Value("${cloudinary.cloud-name}") String cloudName,
                          @Value("${cloudinary.api-key}") String apiKey,
                          @Value("${cloudinary.api-secret}") String apiSecret,
                          @Value("${app.storage.local-dir}") String localDir) {
        this.localDir = localDir;
        this.cloudinary = cloudName.isBlank() || apiKey.isBlank() || apiSecret.isBlank()
                ? null
                : new Cloudinary(ObjectUtils.asMap("cloud_name", cloudName, "api_key", apiKey, "api_secret", apiSecret));
    }

    public String store(MultipartFile file) {
        if (file == null || file.isEmpty()) return null;
        if (!ALLOWED.contains(file.getContentType())) throw new ApiException(HttpStatus.BAD_REQUEST, "Only jpg, jpeg and png images are allowed");
        if (file.getSize() > 5 * 1024 * 1024) throw new ApiException(HttpStatus.BAD_REQUEST, "Maximum image size is 5MB");
        try {
            if (cloudinary != null) {
                Map<?, ?> result = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap("folder", "society-maintenance"));
                return String.valueOf(result.get("secure_url"));
            }
        } catch (Exception ignored) {
            // Fallback to local storage if Cloudinary is configured but unavailable.
        }
        try {
            Files.createDirectories(Path.of(localDir));
            String ext = file.getOriginalFilename() == null ? ".jpg" : file.getOriginalFilename().replaceAll("^.*(\\.[^.]+)$", "$1");
            String name = UUID.randomUUID() + ext;
            Files.copy(file.getInputStream(), Path.of(localDir, name), StandardCopyOption.REPLACE_EXISTING);
            return "/uploads/" + name;
        } catch (Exception ex) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not store image");
        }
    }
}
