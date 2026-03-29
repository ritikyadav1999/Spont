package org.example.spont.auth.security;

import lombok.RequiredArgsConstructor;
import org.example.spont.user.entity.User;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

    private final User user;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public @Nullable String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUserId().toString();
    }

    public String getPhone(){
        return user.getPhone();
    }

    public User getUser() {
        return user;
    }

    public String getEmail(){
        return  user.getEmail();
    }

    public UUID getUserId() {
        return user.getUserId();
    }
}
