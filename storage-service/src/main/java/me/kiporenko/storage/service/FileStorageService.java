package me.kiporenko.storage.service;

import me.kiporenko.storage.model.FileMetadata;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


@Service
public interface FileStorageService {

    public FileMetadata storeFile(MultipartFile file);

    public Page<FileMetadata> listFiles(Pageable pageable);
}