package com.diplom.cloudstorage.services;

import com.diplom.cloudstorage.dto.LoginRequest;
import com.diplom.cloudstorage.exceptions.BadCredentialsException;
import com.diplom.cloudstorage.security.JwtUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private Map<String, String> tokenBlackList = new HashMap<>();

    public AuthService(AuthenticationManager authenticationManager, JwtUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
    }

    public String loginUser(LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    loginRequest.getLogin(), loginRequest.getPassword()));

            SecurityContextHolder.getContext()
                    .setAuthentication(authentication);

            return jwtUtils.generateJwtToken(authentication);
        } catch (AuthenticationException ex) {
            throw new BadCredentialsException("Bad credentials");
        }
    }

    public void logoutUser(String authToken) {
        String jwt = jwtUtils.parseAuthToken(authToken);
        tokenBlackList.put(jwtUtils.getUserNameFromJwtToken(jwt), jwt);
    }

    public boolean isBlacklisted(String username, String token) {
        String blacklistedToken = tokenBlackList.get(username);
        return blacklistedToken != null && blacklistedToken.equals(token);
    }
}
