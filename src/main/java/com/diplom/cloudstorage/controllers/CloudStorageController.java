package com.diplom.cloudstorage.controllers;

import com.diplom.cloudstorage.dto.FileResponse;
import com.diplom.cloudstorage.entites.File;
import com.diplom.cloudstorage.security.JwtUtils;
import com.diplom.cloudstorage.services.CloudStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class CloudStorageController {
    private final CloudStorageService cloudStorageService;
    private final JwtUtils jwtUtils;
    private static final Logger logger = LoggerFactory.getLogger(CloudStorageController.class);

    public CloudStorageController(CloudStorageService cloudStorageService, JwtUtils jwtUtils) {
        this.cloudStorageService = cloudStorageService;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/file")
    public ResponseEntity<?> uploadFile(@RequestHeader("auth-token") String authToken,
                                        @RequestParam("filename") String filename,
                                        @RequestBody MultipartFile file) throws IOException {
        logger.info("Received uploadFile request with authToken: {}, filename: {}", authToken, filename);

        String owner = jwtUtils.getUserNameFromAuthToken(authToken);
        cloudStorageService.uploadFile(owner, filename, file);

        logger.info("File uploaded successfully for owner: {}, filename: {}", owner, filename);
        return ResponseEntity.ok(HttpStatus.OK);

    }

    @DeleteMapping("/file")
    public ResponseEntity<?> deleteFile(@RequestHeader("auth-token") String authToken,
                                        @RequestParam("filename") String filename) {
        logger.info("Received deleteFile request with authToken: {}, filename: {}", authToken, filename);

        String owner = jwtUtils.getUserNameFromAuthToken(authToken);
        cloudStorageService.deleteFile(owner, filename);

        logger.info("File deleted successfully for owner: {}, filename: {}", owner, filename);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping("/file")
    public ResponseEntity<byte[]> downloadFile(@RequestHeader("auth-token") String authToken,
                                               @RequestParam("filename") String filename) {
        logger.info("Received downloadFile request with authToken: {}, filename: {}", authToken, filename);

        String owner = jwtUtils.getUserNameFromAuthToken(authToken);
        File file = cloudStorageService.downloadFile(owner, filename);

        logger.info("File downloaded successfully for owner: {}, filename: {}", owner, filename);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(file.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                .body(file.getData());

    }

    @PutMapping("/file")
    public ResponseEntity<?> renameFile(@RequestHeader("auth-token") String authToken,
                                        @RequestParam("filename") String filename,
                                        @RequestBody Map<String, String> fileNameRequest) {
        logger.info("Received renameFile request with authToken: {}, filename: {}, newFilename: {}", authToken, filename,
                fileNameRequest.get("filename"));

        String owner = jwtUtils.getUserNameFromAuthToken(authToken);
        cloudStorageService.renameFile(owner, filename, fileNameRequest.get("filename"));

        logger.info("File renamed successfully for owner: {}, filename: {}, newFilename: {}", owner, filename,
                fileNameRequest.get("filename"));
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping("/list")
    public ResponseEntity<List<FileResponse>> getAllFiles(@RequestHeader("auth-token") String authToken,
                                                          @RequestParam("limit") int limit) {
        logger.info("Received getAllFiles request with authToken: {}, limit: {}", authToken, limit);

        String owner = jwtUtils.getUserNameFromAuthToken(authToken);
        List<File> files = cloudStorageService.getFiles(owner, limit);
        List<FileResponse> fileResponses = files.stream()
                .map(file -> new FileResponse(file.getFilename(), file.getSize()))
                .collect(Collectors.toList());

        logger.info("Retrieved {} files for owner: {}", fileResponses.size(), owner);
        return ResponseEntity.ok(fileResponses);
    }
}
