package com.diplom.cloudstorage.security;

import com.diplom.cloudstorage.exceptions.UnauthorizedException;
import com.diplom.cloudstorage.services.AuthService;
import com.diplom.cloudstorage.services.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class AuthTokenFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);
    @Autowired
    private AuthService authService;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        logger.info("Entering doFilterInternal method for request: {}", request.getRequestURI());

        try {
            String jwt = parseJwt(request);
            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                logger.debug("JWT token validated successfully");

                String username = jwtUtils.getUserNameFromJwtToken(jwt);
                logger.debug("Username extracted from JWT token: {}", username);

                if (!authService.isWhitelisted(username, jwt)) {
                    logger.error("Token is blacklisted for user: {}", username);
                    throw new UnauthorizedException("Token is blacklisted");
                }

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext()
                        .setAuthentication(authentication);
                logger.info("User authenticated successfully: {}", username);
            }
        } catch (Exception e) {
            logger.error("Error during authentication", e);
            throw new UnauthorizedException(e.getMessage());
        }

        filterChain.doFilter(request, response);
        logger.debug("Exiting doFilterInternal method for request: {}", request.getRequestURI());
    }

    private String parseJwt(HttpServletRequest request) {
        logger.debug("Entering parseJwt method for request: {}", request.getRequestURI());
        String headerAuth = request.getHeader("auth-token");

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            String jwt = headerAuth.substring(7);
            logger.debug("JWT token parsed successfully from header");
            return jwt;
        }

        logger.debug("No valid JWT token found in header");
        return null;
    }
}
