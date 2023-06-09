package com.ubetgpt.betgpt.service;

import com.ubetgpt.betgpt.enums.Provider;
import com.ubetgpt.betgpt.persistence.entity.User;
import com.ubetgpt.betgpt.persistence.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> getByUserEmail(String username) {
        return userRepository.findByEmail(username);
    }

    public void save(User user) {
        userRepository.save(user);
    }
   public void processOAuthPostLogin(String email){
        User existUser = userRepository.findByEmail(email).orElse(null);
       if (existUser == null) {
           User newUser = new User();
           newUser.setEmail(email);
           newUser.setProvider(Provider.GOOGLE);
           userRepository.save(newUser);
       }
   };
}
