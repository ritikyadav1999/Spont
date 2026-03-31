package org.example.spont.auth.security;

import lombok.RequiredArgsConstructor;
import org.example.spont.user.entity.User;
import org.example.spont.user.service.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        User user = userService.findByIdentifier(identifier).orElseThrow();
        return new CustomUserDetails(user);
    }


    public UserDetails loadUserByUserid(String id) throws UsernameNotFoundException {
        User user = userService.findUserById(UUID.fromString(id)).orElseThrow();
        return new CustomUserDetails(user);
    }

}
