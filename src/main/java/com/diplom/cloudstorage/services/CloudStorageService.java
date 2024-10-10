package com.diplom.cloudstorage.services;

import com.diplom.cloudstorage.dto.FileResponse;
import com.diplom.cloudstorage.entites.File;
import com.diplom.cloudstorage.exceptions.FileDeletionException;
import com.diplom.cloudstorage.exceptions.FileUploadException;
import com.diplom.cloudstorage.exceptions.GettingFileListException;
import com.diplom.cloudstorage.exceptions.InvalidInputDataException;
import com.diplom.cloudstorage.repositories.FileRepository;
import com.diplom.cloudstorage.security.JwtUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class CloudStorageService {
    private final JwtUtils jwtUtils;
    private final FileRepository fileRepository;

    public CloudStorageService(JwtUtils jwtUtils, FileRepository fileRepository) {
        this.jwtUtils = jwtUtils;
        this.fileRepository = fileRepository;
    }

    public void uploadFile(String authToken, String filename, MultipartFile file) throws IOException {
        if (filename == null || filename.isEmpty()) {
            throw new InvalidInputDataException("filename is required");
        }
        if (file == null || file.isEmpty()) {
            throw new InvalidInputDataException("file is required");
        }
        String owner = jwtUtils.getUserNameFromAuthToken(authToken);
        fileRepository.save(new File(file.getContentType(), file.getBytes(), filename, owner, file.getSize()));
    }

    public List<FileResponse> getFiles(String authToken, int limit) {
        if (limit <= 0) {
            throw new InvalidInputDataException("limit must be greater than 0");
        }
        try {
            String owner = jwtUtils.getUserNameFromAuthToken(authToken);
            Optional<List<File>> fileList = fileRepository.findAllByOwner(owner);
            return fileList.get().stream().map(fr -> new FileResponse(fr.getFilename(), fr.getSize()))
                    .limit(limit)
                    .collect(Collectors.toList());
        } catch (RuntimeException ex) {
            throw new GettingFileListException("Error getting file list");
        }
    }

    public void deleteFile(String authToken, String filename) {
        if (filename == null || filename.isEmpty()) {
            throw new InvalidInputDataException("filename is required");
        }
        String owner = jwtUtils.getUserNameFromAuthToken(authToken);
        try {
            fileRepository.removeByFilenameAndOwner(filename, owner);
        } catch (RuntimeException ex) {
            throw new FileDeletionException("Error delete file");
        }
    }

    public File downloadFile(String authToken, String filename) {
        if (filename == null || filename.isEmpty()) {
            throw new InvalidInputDataException("filename is required");
        }
        String owner = jwtUtils.getUserNameFromAuthToken(authToken);
        try {
            return fileRepository.findByFilenameAndOwner(filename, owner);
        } catch (RuntimeException ex) {
            throw new FileUploadException("Error upload file");
        }
    }

    public void renameFile(String authToken, String filename, String newFilename) {
        if (filename == null || filename.isEmpty()) {
            throw new InvalidInputDataException("filename is required");
        }
        if (newFilename == null || newFilename.isEmpty()) {
            throw new InvalidInputDataException("new filename is required");
        }
        String owner = jwtUtils.getUserNameFromAuthToken(authToken);
        try {
            fileRepository.renameFile(filename, newFilename, owner);
        } catch (RuntimeException ex) {
            throw new FileUploadException("Error upload file");
        }
    }
}
