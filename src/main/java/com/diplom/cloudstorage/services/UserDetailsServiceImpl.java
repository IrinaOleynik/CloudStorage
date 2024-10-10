package com.diplom.cloudstorage.services;

import com.diplom.cloudstorage.entites.User;
import com.diplom.cloudstorage.exceptions.BadCredentialsException;
import com.diplom.cloudstorage.repositories.UserRepository;
import com.diplom.cloudstorage.security.MyUserPrincipal;
import jakarta.transaction.Transactional;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        return new MyUserPrincipal(user);
    }
}

