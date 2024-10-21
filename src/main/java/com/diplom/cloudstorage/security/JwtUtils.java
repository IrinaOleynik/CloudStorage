package com.diplom.cloudstorage.security;

import com.diplom.cloudstorage.exceptions.UnauthorizedException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${jwt.signingKey}")
    private String jwtSecret;

    @Value("${jwt.ExpirationMs}")
    private int jwtExpirationMs;

    public String generateJwtToken(Authentication authentication) {
        logger.info("Entering generateJwtToken method");

        MyUserPrincipal userPrincipal = (MyUserPrincipal) authentication.getPrincipal();
        Map<String, Object> claims = new HashMap<>();
        String username = userPrincipal.getUsername();
        claims.put("email", username);

        logger.info("JWT token generated successfully for user: {}", username);
        return Jwts.builder()
                .claims(claims)
                .setSubject(userPrincipal.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(getSigningKey())
                .compact();

    }

    public String parseAuthToken(String token) {
        logger.debug("Entering parseAuthToken method with token: {}", token);

        if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
            String jwt = token.substring(7);
            logger.debug("Auth token parsed successfully");
            return jwt;
        }

        logger.warn("Invalid auth token format");
        return null;
    }

    private SecretKey getSigningKey() {
        logger.debug("Entering getSigningKey method");

        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);

        logger.debug("Signing key generated successfully");
        return Keys.hmacShaKeyFor(keyBytes);

    }

    public String getUserNameFromJwtToken(String jwt) {
        logger.debug("Entering getUserNameFromJwtToken method with jwt: {}", jwt);

        String username = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(jwt)
                .getPayload()
                .getSubject();

        logger.debug("Username extracted successfully from JWT token: {}", username);
        return username;
    }

    public String getUserNameFromAuthToken(String authToken) {
        logger.debug("Entering getUserNameFromAuthToken method with authToken: {}", authToken);

        String jwt = parseAuthToken(authToken);
        String username = getUserNameFromJwtToken(jwt);

        logger.debug("Username extracted successfully from auth token: {}", username);
        return username;
    }

    public boolean validateJwtToken(String jwt) {
        logger.debug("Entering validateJwtToken method with jwt: {}", jwt);
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(jwt);
            logger.debug("JWT token validated successfully");
            return true;
        } catch (SignatureException | ExpiredJwtException | UnsupportedJwtException | MalformedJwtException |
                 IllegalArgumentException e) {
            logger.error("JWT token validation has failed", e);
            throw new UnauthorizedException("JWT token validation has failed");
        }

    }

}