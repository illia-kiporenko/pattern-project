package me.kiporenko.storage.controller;

import me.kiporenko.storage.model.FileMetadata;
import me.kiporenko.storage.service.impl.FileStorageServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
@RequestMapping("/files")
public class FileController {

    private final FileStorageServiceImpl fileStorageService;

    @Autowired
    public FileController(FileStorageServiceImpl fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @PostMapping("/upload")
    public FileMetadata uploadFile(@RequestParam("file") MultipartFile file) {
        return fileStorageService.storeFile(file);
    }

    @PostMapping("/uploadMultiple")
    public List<FileMetadata> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files) {
        return Arrays.stream(files)
                .map(fileStorageService::storeFile)
                .collect(Collectors.toList());
    }

    @GetMapping("/download/{fileId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long fileId) {
        FileStorageServiceImpl.FileDownloadPayload payload = fileStorageService.loadFileAsResource(fileId);
        Resource resource = payload.getResource();
        FileMetadata metadata = payload.getMetadata();

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(metadata.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + metadata.getOriginalFilename() + "\"")
                .body(resource);
    }

    @GetMapping("/download/zip")
    public ResponseEntity<Resource> downloadFilesAsZip(@RequestParam("ids") List<Long> fileIds) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            for (Long fileId : fileIds) {
                FileStorageServiceImpl.FileDownloadPayload payload = fileStorageService.loadFileAsResource(fileId);
                Resource resource = payload.getResource();
                FileMetadata metadata = payload.getMetadata();

                ZipEntry zipEntry = new ZipEntry(metadata.getOriginalFilename());
                zos.putNextEntry(zipEntry);
                zos.write(resource.getInputStream().readAllBytes());
                zos.closeEntry();
            }
        }

        ByteArrayResource resource = new ByteArrayResource(baos.toByteArray());

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"files.zip\"")
                .body(resource);
    }


    @GetMapping
    public Page<FileMetadata> listAllFiles(Pageable pageable) {
        return fileStorageService.listFiles(pageable);
    }
}