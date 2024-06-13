package com.ead.authuser.api.configs.security;

import com.ead.authuser.domain.exceptions.UserNotFoundException;
import com.ead.authuser.domain.models.UserModel;
import com.ead.authuser.domain.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserDetailServiceImpl implements UserDetailsService {

    public static final String NOT_FOUND_WITH_USERNAME = "User Not found with username: ";
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserModel userModel = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(NOT_FOUND_WITH_USERNAME + username));
        return UserDetailsImpl.build(userModel);
    }
}
