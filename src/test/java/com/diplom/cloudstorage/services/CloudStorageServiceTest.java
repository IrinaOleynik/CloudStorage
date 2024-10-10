package com.diplom.cloudstorage.services;

import com.diplom.cloudstorage.dto.FileResponse;
import com.diplom.cloudstorage.entites.File;
import com.diplom.cloudstorage.exceptions.InvalidInputDataException;
import com.diplom.cloudstorage.repositories.FileRepository;
import com.diplom.cloudstorage.security.JwtUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CloudStorageServiceTest {

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private FileRepository fileRepository;

    @InjectMocks
    private CloudStorageService cloudStorageService;

    private final String authToken = UUID.randomUUID().toString();
    private final String filename = "test.txt";
    private final MultipartFile file = new MockMultipartFile(
            "file", filename, "text/plain", "Hello World".getBytes());
    private final String owner = "user";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void uploadFileSuccess() throws IOException {

        when(jwtUtils.getUserNameFromAuthToken(authToken)).thenReturn(owner);

        cloudStorageService.uploadFile(authToken, filename, file);

        verify(fileRepository, times(1)).save(any(File.class));
    }

    @Test
    void testUploadFileInvalidInput() throws IOException {

        assertThrows(InvalidInputDataException.class, () -> cloudStorageService.uploadFile(authToken, null, null));
        verify(fileRepository, never()).save(any(File.class));
    }

    @Test
    void getFilesSuccess() {

        List<File> fileList = Arrays.asList(
                new File("text/plain", "Hello".getBytes(), "file1.txt", owner, 100L),
                new File("text/plain", "World".getBytes(), "file2.txt", owner, 200L)
        );

        when(jwtUtils.getUserNameFromAuthToken(authToken)).thenReturn(owner);
        when(fileRepository.findAllByOwner(owner)).thenReturn(Optional.of(fileList));

        int limit = 10;
        List<FileResponse> result = cloudStorageService.getFiles(authToken, limit);

        assertEquals(2, result.size());
        verify(fileRepository, times(1)).findAllByOwner(owner);
    }

    @Test
    void testGetFilesInvalidInput() {

        assertThrows(InvalidInputDataException.class, () -> cloudStorageService.getFiles(authToken, 0));
        verify(fileRepository, never()).findAllByOwner(anyString());
    }

    @Test
    void testDeleteFileSuccess() {

        when(jwtUtils.getUserNameFromAuthToken(authToken)).thenReturn(owner);

        cloudStorageService.deleteFile(authToken, filename);

        verify(fileRepository, times(1)).removeByFilenameAndOwner(filename, owner);
    }

    @Test
    void testDeleteFileInvalidInput() {

        assertThrows(InvalidInputDataException.class, () -> cloudStorageService.deleteFile(authToken, null));
        verify(fileRepository, never()).removeByFilenameAndOwner(anyString(), anyString());
    }

    @Test
    void testDownloadFileSuccess() {

        File file = new File("text/plain", "Hello".getBytes(), filename, owner, 100L);

        when(jwtUtils.getUserNameFromAuthToken(authToken)).thenReturn(owner);
        when(fileRepository.findByFilenameAndOwner(filename, owner)).thenReturn(file);

        File result = cloudStorageService.downloadFile(authToken, filename);

        assertNotNull(result);
        assertEquals(filename, result.getFilename());
        verify(fileRepository, times(1)).findByFilenameAndOwner(filename, owner);
    }

    @Test
    void testDownloadFileInvalidInput() {

        assertThrows(InvalidInputDataException.class, () -> cloudStorageService.downloadFile(authToken, null));
        verify(fileRepository, never()).findByFilenameAndOwner(anyString(), anyString());
    }

    @Test
    void testRenameFileSuccess() {

        String newFilename = "newTest.txt";

        when(jwtUtils.getUserNameFromAuthToken(authToken)).thenReturn(owner);

        cloudStorageService.renameFile(authToken, filename, newFilename);

        verify(fileRepository, times(1)).renameFile(filename, newFilename, owner);
    }

    @Test
    void testRenameFileInvalidInput() {

        assertThrows(InvalidInputDataException.class, () -> cloudStorageService.renameFile(authToken, null, null));
        verify(fileRepository, never()).renameFile(anyString(), anyString(), anyString());
    }
}