package com.ubetgpt.betgpt.controller;

import com.ubetgpt.betgpt.model.chat.*;
import com.ubetgpt.betgpt.persistence.entity.Order;
import com.ubetgpt.betgpt.persistence.repository.OrderDAO;
import com.ubetgpt.betgpt.service.ChatCompletionService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/")
public class MainController {
    @Resource
    private OrderDAO orderDAO;
    private final Logger logger = org.slf4j.LoggerFactory.getLogger(MainController.class);
    @Resource
    private ChatCompletionService chatCompletionService;
    @Value("${openai.model}")
    private String model;
    @Value("${openai.danmode-msg}")
    private String danModeMsg;
    @Value("${stripe.keys.public}")
    private String stripePublicKey;

    @GetMapping
    public String home(Model model, Authentication authentication){
        model.addAttribute("stripePublicKey",stripePublicKey);
        if(authentication!= null && authentication.getPrincipal() instanceof DefaultOAuth2User) {
            DefaultOAuth2User user = (DefaultOAuth2User) authentication.getPrincipal();
            model.addAttribute("userDetails", user.getAttribute("name")!= null ?user.getAttribute("name"):user.getAttribute("login"));
            Order order = orderDAO.findByUserEmail(user.getAttribute("email"));
            model.addAttribute("paid", order!=null);
        }else if(authentication!= null && authentication.getPrincipal() instanceof User){
            User user = (User) authentication.getPrincipal();
//            com.oauth.implementation.model.User users = userRepo.findByEmail(user.getUsername());
            model.addAttribute("userDetails", user.getUsername());
            Order order = orderDAO.findByUserEmail(user.getUsername());
            model.addAttribute("paid", order!=null);
        }
        return "index";
    }

    @GetMapping("home")
    public String homePage(Model model, Authentication authentication){
        return home(model, authentication);
    }

    @PostMapping("chat")
    @ResponseBody
    public ChatResponse chatPage(@RequestBody String prompt){
        ChatCompletionRequest request = new ChatCompletionRequest();
        request.setModel(model);

        List<ChatMessage> messages = new ArrayList<>();
        messages.add(new ChatMessage("user", danModeMsg));
        messages.add(new ChatMessage("user", prompt));
        request.setMessages(messages);

//        ChatResponse response = chatCompletionService.createChatCompletion(request);
        ArrayList<Choice> choices  = new ArrayList<>();
        Message message = new Message("user", "GPT: Hi there! Dan: Hi I am Dan!");
        choices.add(new Choice(message, "stop", 0));
        ChatResponse response = ChatResponse.builder()
                .id("cmpl-3QJ5")
                .created(1627777777)
                .model("davinci:2020-05-03")
                .object("text_completion")
                .usage(new Usage())
                .choices(choices).build();
        logger.info("Response: {}", response.toString());
        return response;
    }
}
