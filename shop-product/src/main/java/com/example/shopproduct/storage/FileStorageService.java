package com.example.shopproduct.storage;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path root = Paths.get("uploads");

    public String save(MultipartFile file) throws IOException {
        if (!Files.exists(root)) {
            Files.createDirectories(root);
        }
        String filename = UUID.randomUUID() + "-" + file.getOriginalFilename();
        Path filePath = root.resolve(filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        return "/uploads/" + filename;
    }
}
