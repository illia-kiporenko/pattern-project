package me.kiporenko.storage.service.impl;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityNotFoundException;
import lombok.Getter;
import me.kiporenko.storage.config.FileStorageProperties;
import me.kiporenko.storage.exception.FileStorageException;
import me.kiporenko.storage.model.FileMetadata;
import me.kiporenko.storage.repository.FileMetadataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;

@Service
public class FileStorageServiceImpl {

    private final Path fileStorageLocation;
    private final FileMetadataRepository fileMetadataRepository;

    @Autowired
    public FileStorageServiceImpl(FileStorageProperties fileStorageProperties, FileMetadataRepository fileMetadataRepository) {
        this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir()).toAbsolutePath().normalize();
        this.fileMetadataRepository = fileMetadataRepository;
    }

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    public FileMetadata storeFile(MultipartFile file) {
        String originalFilename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));

        try {
            // Security check for invalid characters
            if (originalFilename.contains("..")) {
                throw new FileStorageException("Filename contains invalid path sequence " + originalFilename);
            }

            // Generate a unique filename to prevent collisions
            String extension = StringUtils.getFilenameExtension(originalFilename);
            String storedFilename = UUID.randomUUID() + (extension != null ? "." + extension : "");

            // Copy file to the target location (Replacing existing file with the same name)
            Path targetLocation = this.fileStorageLocation.resolve(storedFilename);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            // Save metadata to the database
            FileMetadata metadata = new FileMetadata(originalFilename, storedFilename, file.getContentType(), file.getSize());
            return fileMetadataRepository.save(metadata);

        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + originalFilename + ". Please try again!", ex);
        }
    }

    public FileDownloadPayload loadFileAsResource(Long fileId) {
        FileMetadata metadata = fileMetadataRepository.findById(fileId)
                .orElseThrow(() -> new EntityNotFoundException("File"+ fileId));

        try {
            Path filePath = this.fileStorageLocation.resolve(metadata.getStoredFilename()).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return new FileDownloadPayload(resource, metadata);
            } else {
                throw new FileStorageException("File not found or cannot be read: " + metadata.getOriginalFilename());
            }
        } catch (MalformedURLException ex) {
            throw new FileStorageException("File not found: " + metadata.getOriginalFilename(), ex);
        }
    }

    public Page<FileMetadata> listFiles(Pageable pageable) {
        return fileMetadataRepository.findAll(pageable);
    }

    // A simple wrapper class to return both the Resource and its metadata
    @Getter
    public static class FileDownloadPayload {
        private final Resource resource;
        private final FileMetadata metadata;

        public FileDownloadPayload(Resource resource, FileMetadata metadata) {
            this.resource = resource;
            this.metadata = metadata;
        }
    }
}