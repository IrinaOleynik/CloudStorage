package com.diplom.cloudstorage.services;

import com.diplom.cloudstorage.dto.LoginRequest;
import com.diplom.cloudstorage.security.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class AuthServiceTest {
    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private AuthService authService;

    private final String username = "user";
    private final String password = "password";
    private final LoginRequest loginRequest = new LoginRequest(username, password);
    private final String expectedToken = UUID.randomUUID().toString();


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLoginUserSuccess() {

        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtUtils.generateJwtToken(authentication)).thenReturn(expectedToken);

        String actualToken = authService.loginUser(loginRequest);

        assertNotNull(actualToken);
        assertEquals(expectedToken, actualToken);
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtils, times(1)).generateJwtToken(authentication);
    }

    @Test
    void testLoginUserFailure() {

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThrows(com.diplom.cloudstorage.exceptions.BadCredentialsException.class, () -> authService.loginUser(loginRequest));
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtils, never()).generateJwtToken(any(Authentication.class));
    }

    @Test
    void testLogoutUser() {

        String authToken = "Bearer " + expectedToken;
        when(jwtUtils.parseAuthToken(authToken)).thenReturn(expectedToken);
        when(jwtUtils.getUserNameFromJwtToken(expectedToken)).thenReturn(username);

        authService.logoutUser(authToken);

        assertTrue(authService.isBlacklisted(username, expectedToken));
        verify(jwtUtils, times(1)).parseAuthToken(authToken);
        verify(jwtUtils, times(1)).getUserNameFromJwtToken(expectedToken);
    }

}