package com.ubetgpt.betgpt.service;

import com.ubetgpt.betgpt.model.MyUserDetails;
import com.ubetgpt.betgpt.persistence.entity.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MyUserDetailsService implements UserDetailsService {
    private final UserService userService;

    public MyUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userService.getByUserEmail(username);
        if (user.isPresent()){
            return new MyUserDetails(user.get());
        }
        throw  new UsernameNotFoundException("User '" + username + "' not found");
    }


}
