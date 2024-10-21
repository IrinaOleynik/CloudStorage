package com.diplom.cloudstorage.services;

import com.diplom.cloudstorage.entites.File;
import com.diplom.cloudstorage.exceptions.FileDeletionException;
import com.diplom.cloudstorage.exceptions.FileUploadException;
import com.diplom.cloudstorage.exceptions.GettingFileListException;
import com.diplom.cloudstorage.exceptions.InvalidInputDataException;
import com.diplom.cloudstorage.repositories.FileRepository;
import com.diplom.cloudstorage.security.JwtUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class CloudStorageService {
    private final FileRepository fileRepository;
    private static Logger logger = LoggerFactory.getLogger(CloudStorageService.class);

    public CloudStorageService(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    public void uploadFile(String owner, String filename, MultipartFile file) throws IOException {
        logger.info("Entering uploadFile method with owner: {}, filename: {}", owner, filename);
        if (filename == null || filename.isEmpty()) {
            logger.error("Invalid input data: filename is required");
            throw new InvalidInputDataException("filename is required");
        }
        if (file == null || file.isEmpty()) {
            logger.error("Invalid input data: file is required");
            throw new InvalidInputDataException("file is required");
        }
        fileRepository.save(new File(file.getContentType(), file.getBytes(), filename, owner, file.getSize()));
        logger.info("File uploaded successfully for owner: {}, filename: {}", owner, filename);
    }

    public List<File> getFiles(String owner, int limit) {
        logger.info("Entering getFiles method with owner: {}, limit: {}", owner, limit);
        if (limit <= 0) {
            throw new InvalidInputDataException("limit must be greater than 0");
        }
        try {
            Optional<List<File>> fileList = fileRepository.findAllByOwner(owner);
            List<File> files = fileList.orElse(Collections.emptyList()).stream()
                    .limit(limit)
                    .collect(Collectors.toList());
            logger.info("Retrieved {} files for owner: {}", files.size(), owner);
            return files;
        } catch (RuntimeException ex) {
            logger.error("Error getting file list for owner: {}", owner, ex);
            throw new GettingFileListException("Error getting file list");
        }
    }

    public void deleteFile(String owner, String filename) {
        logger.info("Entering deleteFile method with owner: {}, filename: {}", owner, filename);
        if (filename == null || filename.isEmpty()) {
            logger.error("Invalid input data: filename is required");
            throw new InvalidInputDataException("filename is required");
        }
        try {
            fileRepository.removeByFilenameAndOwner(filename, owner);
            logger.info("File deleted successfully for owner: {}, filename: {}", owner, filename);
        } catch (RuntimeException ex) {
            logger.error("Error deleting file for owner: {}, filename: {}", owner, filename, ex);
            throw new FileDeletionException("Error delete file");
        }
    }

    public File downloadFile(String owner, String filename) {
        logger.info("Entering downloadFile method with owner: {}, filename: {}", owner, filename);
        if (filename == null || filename.isEmpty()) {
            logger.error("Invalid input data: filename is required");
            throw new InvalidInputDataException("filename is required");
        }
        try {
            File file = fileRepository.findByFilenameAndOwner(filename, owner);
            logger.info("File downloaded successfully for owner: {}, filename: {}", owner, filename);
            return file;
        } catch (RuntimeException ex) {
            logger.error("Error downloading file for owner: {}, filename: {}", owner, filename, ex);
            throw new FileUploadException("Error upload file");
        }
    }

    public void renameFile(String owner, String filename, String newFilename) {
        logger.info("Entering renameFile method with owner: {}, filename: {}, newFilename: {}", owner, filename, newFilename);
        if (filename == null || filename.isEmpty()) {
            logger.error("Invalid input data: filename is required");
            throw new InvalidInputDataException("filename is required");
        }
        if (newFilename == null || newFilename.isEmpty()) {
            logger.error("Invalid input data: new filename is required");
            throw new InvalidInputDataException("new filename is required");
        }
        try {
            fileRepository.renameFile(filename, newFilename, owner);
            logger.info("File renamed successfully for owner: {}, filename: {}, newFilename: {}", owner, filename, newFilename);
        } catch (RuntimeException ex) {
            logger.error("Error renaming file for owner: {}, filename: {}, newFilename: {}", owner, filename, newFilename, ex);
            throw new FileUploadException("Error upload file");
        }
    }
}
