package com.diplom.cloudstorage.controllers;

import com.diplom.cloudstorage.SpringBootApplicationTest;
import com.diplom.cloudstorage.dto.FileResponse;
import com.diplom.cloudstorage.repositories.FileRepository;
import com.diplom.cloudstorage.services.CloudStorageService;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CloudStorageControllerTest extends SpringBootApplicationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CloudStorageService cloudStorageService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FileRepository fileRepository;

    private final String FILE_PATH = "/file";
    private final String LIST_PATH = "/list";
    private final String filename = "test.txt";
    private final MockMultipartFile file = new MockMultipartFile("file", filename,
            MediaType.TEXT_PLAIN_VALUE, "Hello, World!".getBytes());
    private final String validToken = "Bearer eyJhbGciOiJIUzM4NCJ9.eyJlbWFpbCI6InVzZXIxQGdtYWlsLmNvbSIsInN1YiI6InVzZXI" +
            "xQGdtYWlsLmNvbSIsImlhdCI6MTcyODIyMTQ1Nn0.w-gTpu7rUil8i-mbTngbaI7rpKNBnoTxLNxySN5aLKEQNmgUzl0Mas5B1iZJr3L4";

    @BeforeEach
    void setUp() throws Exception {
        fileRepository.deleteAll();
        cloudStorageService.uploadFile(validToken, filename, file);
    }

    @Test
    void uploadFile() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.multipart(FILE_PATH)
                        .file(file)
                        .param("filename", filename)
                        .header("auth-token", validToken))
                .andExpect(status().isOk());
    }


    @Test
    void deleteFile() throws Exception {
        mockMvc.perform(delete(FILE_PATH)
                        .param("filename", filename)
                        .header("auth-token", validToken))
                .andExpect(status().isOk());
    }

    @Test
    void downloadFile() throws Exception {
        mockMvc.perform(get(FILE_PATH)
                        .param("filename", filename)
                        .header("auth-token", validToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.TEXT_PLAIN))
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"test.txt\""))
                .andExpect(content().bytes("Hello, World!".getBytes()));
    }

    @Test
    void renameFile() throws Exception {
        Map<String, String> fileNameRequest = new HashMap<>();
        fileNameRequest.put("filename", "newTest.txt");

        mockMvc.perform(put(FILE_PATH)
                        .param("filename", filename)
                        .header("auth-token", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(fileNameRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void getAllFiles() throws Exception {
        List<FileResponse> files = Arrays.asList(
                new FileResponse(filename, file.getSize())
        );

        mockMvc.perform(get(LIST_PATH)
                        .param("limit", "10")
                        .header("auth-token", validToken))
                .andExpect(status().isOk())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(files)));
    }
}