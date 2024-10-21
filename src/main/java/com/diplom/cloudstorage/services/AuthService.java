package com.diplom.cloudstorage.services;

import com.diplom.cloudstorage.exceptions.BadCredentialsException;
import com.diplom.cloudstorage.security.JwtUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private Map<String, String> tokenWhiteList;
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    public AuthService(AuthenticationManager authenticationManager, JwtUtils jwtUtils, Map<String, String> tokenWhiteList) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.tokenWhiteList = tokenWhiteList;
    }

    public String loginUser(String login, String password) {
        logger.info("Entering loginUser method with login: {}", login);
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    login, password));

            SecurityContextHolder.getContext()
                    .setAuthentication(authentication);

            String jwt = jwtUtils.generateJwtToken(authentication);
            tokenWhiteList.put(login, jwt);
            logger.info("User logged in successfully: {}", login);
            return jwt;
        } catch (AuthenticationException ex) {
            logger.error("Bad credentials for login: {}", login, ex);
            throw new BadCredentialsException("Bad credentials");
        }
    }

    public void logoutUser(String owner) {
        logger.info("Entering logoutUser method with owner: {}", owner);
        tokenWhiteList.remove(owner);
        logger.info("User logged out successfully: {}", owner);
    }

    public boolean isWhitelisted(String username, String token) {
        logger.info("Entering isWhitelisted method with username: {}", username);
        String blacklistedToken = tokenWhiteList.get(username);
        boolean isWhitelisted = blacklistedToken != null && blacklistedToken.equals(token);
        logger.info("User whitelisted status: {} for username: {}", isWhitelisted, username);
        return isWhitelisted;
    }
}
