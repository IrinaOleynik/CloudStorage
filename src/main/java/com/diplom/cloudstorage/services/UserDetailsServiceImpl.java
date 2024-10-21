package com.diplom.cloudstorage.services;

import com.diplom.cloudstorage.entites.User;
import com.diplom.cloudstorage.exceptions.BadCredentialsException;
import com.diplom.cloudstorage.repositories.UserRepository;
import com.diplom.cloudstorage.security.MyUserPrincipal;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("Entering loadUserByUsername method with username: {}", username);
        User user = userRepository.findByUsername(username);
        if (user == null) {
            logger.error("Username Not Found: User not found with username: " + username);
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        logger.info("User loaded by username successfully: {}", username);
        return new MyUserPrincipal(user);
    }
}

