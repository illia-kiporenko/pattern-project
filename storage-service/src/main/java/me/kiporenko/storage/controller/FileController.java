package me.kiporenko.storage.controller;

import jakarta.servlet.http.HttpServletRequest;
import me.kiporenko.storage.model.FileMetadata;
import me.kiporenko.storage.service.impl.FileStorageServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/files") // Removed /api/v1 for simplicity, can be added back in application.yml
public class FileController {

    private final FileStorageServiceImpl fileStorageService;

    @Autowired
    public FileController(FileStorageServiceImpl fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    // === TRADITIONAL SERVLET ENDPOINTS ===

    // 1. Upload Small File (Standard)
    @PostMapping("/upload-small")
    public FileMetadata uploadSmallFile(@RequestParam("file") MultipartFile file) {
        return fileStorageService.storeSmallFile(file);
    }

    // 2. Upload Large File (Streaming)
    @PostMapping(value = "/upload-large", consumes = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public FileMetadata uploadLargeFile(HttpServletRequest request) throws IOException {
        String fileName = request.getHeader("X-File-Name");
        String contentType = request.getContentType();
        long size = request.getContentLengthLong();
        return fileStorageService.storeLargeFile(request.getInputStream(), fileName, contentType, size);
    }

    // 3 & 4. Download Small/Large File (Streaming)
    // This one endpoint is efficient for both small and large files.
    @GetMapping("/download/{fileId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long fileId) {
        FileStorageServiceImpl.FileDownloadPayload payload = fileStorageService.loadFileAsResource(fileId);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(payload.getMetadata().getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + payload.getMetadata().getOriginalFilename() + "\"")
                .body(payload.getResource());
    }

    // === REACTIVE WEBFLUX ENDPOINTS ===

    // 5 & 6. Upload Small/Large File (Reactive)
    // This single reactive endpoint is efficient for both small and large files.
    @PostMapping(value = "/reactive/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<FileMetadata> uploadFileReactive(@RequestPart("file") Mono<FilePart> filePartMono) {
        return filePartMono.flatMap(fileStorageService::storeFileReactive);
    }

    // 7 & 8. Download Small/Large File (Reactive)
    // This single reactive endpoint is efficient for both small and large files.
    @GetMapping(value = "/reactive/download/{fileId}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public Flux<DataBuffer> downloadFileReactive(@PathVariable Long fileId) {
        return fileStorageService.loadFileAsFlux(fileId);
    }

    // --- Utility Endpoints (Existing) ---
    @GetMapping
    public Page<FileMetadata> listAllFiles(Pageable pageable) {
        return fileStorageService.listFiles(pageable);
    }
}