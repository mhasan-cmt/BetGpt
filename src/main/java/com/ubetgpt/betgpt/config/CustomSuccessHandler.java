package com.ubetgpt.betgpt.config;

import com.ubetgpt.betgpt.enums.Provider;
import com.ubetgpt.betgpt.persistence.entity.Roles;
import com.ubetgpt.betgpt.persistence.entity.User;
import com.ubetgpt.betgpt.persistence.repository.UserRepository;
import com.ubetgpt.betgpt.service.MyUserDetailsService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    UserRepository userRepo;

    @Autowired
    MyUserDetailsService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        String redirectUrl = null;
        if(authentication.getPrincipal() instanceof DefaultOAuth2User) {
            DefaultOAuth2User  userDetails = (DefaultOAuth2User ) authentication.getPrincipal();
            String username = userDetails.getAttribute("email") !=null?userDetails.getAttribute("email"):userDetails.getAttribute("login")+"@gmail.com" ;
            if(userRepo.findByEmail(username).isEmpty()) {
                User user = new User();
                user.setEmail(userDetails.getAttribute("email") !=null?userDetails.getAttribute("email"):userDetails.getAttribute("login"));
                user.setProvider(Provider.GOOGLE);
                user.setPassword(("TEST"));
                user.setRole(Roles.ROLE_USER);
                userRepo.save(user);
            }
        }  redirectUrl = "/home";
        new DefaultRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }

}
