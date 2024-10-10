package com.diplom.cloudstorage.controllers;

import com.diplom.cloudstorage.SpringBootApplicationTest;
import com.diplom.cloudstorage.dto.LoginRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthControllerTest extends SpringBootApplicationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private final String LOGIN_PATH = "/login";
    private final String LOGOUT_PATH = "/logout";
    private final String LOGIN = "user1@gmail.com";
    private final String BAD_LOGIN = "login";
    private final String PASSWORD = "user1";
    private final String validToken = "Bearer eyJhbGciOiJIUzM4NCJ9.eyJlbWFpbCI6InVzZXIxQGdtYWlsLmNvbSIsInN1YiI6InVzZXI" +
            "xQGdtYWlsLmNvbSIsImlhdCI6MTcyODIyMTQ1Nn0.w-gTpu7rUil8i-mbTngbaI7rpKNBnoTxLNxySN5aLKEQNmgUzl0Mas5B1iZJr3L4";


    @Test
    void loginSuccess() throws Exception {
        LoginRequest loginRequest = new LoginRequest(LOGIN, PASSWORD);
        mockMvc.perform(post(LOGIN_PATH)
                        .content(objectMapper.writeValueAsString(loginRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.auth-token").isNotEmpty());
    }

    @Test
    void loginBadCredentials() throws Exception {
        LoginRequest loginRequest = new LoginRequest(BAD_LOGIN, PASSWORD);
        mockMvc.perform(post(LOGIN_PATH)
                        .content(objectMapper.writeValueAsString(loginRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void logoutSuccess() throws Exception {
        mockMvc.perform(post(LOGOUT_PATH)
                        .header("auth-token", validToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}