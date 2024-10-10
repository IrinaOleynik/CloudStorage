package com.diplom.cloudstorage.services;

import com.diplom.cloudstorage.entites.User;
import com.diplom.cloudstorage.repositories.UserRepository;
import com.diplom.cloudstorage.security.MyUserPrincipal;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    private final String username = "testUser";
    private final String password = "password";
    private final User user = new User(username, password);
    private final MyUserPrincipal userPrincipal = new MyUserPrincipal(user);

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLoadUserByUsername() {

        when(userRepository.findByUsername(username)).thenReturn(user);

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        assertNotNull(userDetails);
        assertEquals(userPrincipal.getUsername(), userDetails.getUsername());
        assertEquals(userPrincipal.getPassword(), userDetails.getPassword());
        verify(userRepository, times(1)).findByUsername(username);
    }
}