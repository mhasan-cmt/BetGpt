package com.ubetgpt.betgpt.controller;

import com.ubetgpt.betgpt.model.chat.*;
import com.ubetgpt.betgpt.persistence.entity.ChatHistory;
import com.ubetgpt.betgpt.persistence.entity.Order;
import com.ubetgpt.betgpt.persistence.repository.OrderDAO;
import com.ubetgpt.betgpt.service.ChatCompletionService;
import com.ubetgpt.betgpt.service.ChatHistoryService;
import com.ubetgpt.betgpt.service.UserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
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
    @Resource
    private UserService userService;
    @Resource
    private ChatHistoryService chatHistoryService;

    @GetMapping
    public String home(Model model, Authentication authentication) {
        model.addAttribute("stripePublicKey", stripePublicKey);
        if (authentication != null && authentication.getPrincipal() instanceof DefaultOAuth2User) {
            DefaultOAuth2User user = (DefaultOAuth2User) authentication.getPrincipal();
            model.addAttribute("userDetails", user.getAttribute("name") != null ? user.getAttribute("name") : user.getAttribute("login"));
            Order order = orderDAO.findByUserEmail(user.getAttribute("email"));
            model.addAttribute("paid", order != null);
        } else if (authentication != null && authentication.getPrincipal() instanceof User) {
            User user = (User) authentication.getPrincipal();
//            com.oauth.implementation.model.User users = userRepo.findByEmail(user.getUsername());
            model.addAttribute("userDetails", user.getUsername());
            Order order = orderDAO.findByUserEmail(user.getUsername());
            model.addAttribute("paid", order != null);
        }
        return "index";
    }

    @GetMapping("home")
    public String homePage(Model model, Authentication authentication) {
        return home(model, authentication);
    }

    @PostMapping("chat")
    @ResponseBody
    public ChatResponse chatPage(@RequestBody String prompt, Authentication authentication) {
        ChatCompletionRequest request = new ChatCompletionRequest();
        List<ChatMessage> messages = new ArrayList<>();
        messages.add(new ChatMessage("user", danModeMsg));
        request.setModel(model);
        DefaultOAuth2User user = (DefaultOAuth2User) authentication.getPrincipal();
        com.ubetgpt.betgpt.persistence.entity.User user1 = userService.getByUserEmail(user.getAttribute("email")).orElseThrow();

        List<ChatHistory> chatHistory = chatHistoryService.getChatHistoryByUser(user1.getId());

        if (chatHistory != null && chatHistory.size() > 0) {
            chatHistory.forEach(chatHistory1 -> {
                messages.add(new ChatMessage("user", chatHistory1.getUserMessage()));
                messages.add(new ChatMessage("assistant", chatHistory1.getGptMessage()));
            });
        }

        messages.add(new ChatMessage("user", prompt));
        request.setMessages(messages);

        ChatHistory chatHistory1 = new ChatHistory();
        chatHistory1.setUserMessage(prompt);
        chatHistory1.setUser(user1);


        ChatResponse response = chatCompletionService.createChatCompletion(request);
//        ArrayList<Choice> choices = new ArrayList<>();
//        Message message = new Message("user", "GPT: Hi there! Dan: Hi I am Dan!");
//        choices.add(new Choice(message, "stop", 0));
//        ChatResponse response = ChatResponse.builder()
//                .id("cmpl-3QJ5")
//                .created(1627777777)
//                .model("davinci:2020-05-03")
//                .object("text_completion")
//                .usage(new Usage())
//                .choices(choices).build();

        chatHistory1.setGptMessage(response.getChoices().get(0).getMessage().content);
        chatHistory1.setCreated(LocalDateTime.now());
        chatHistoryService.saveChatHistory(chatHistory1);
        logger.info("Response: {}", response.toString());
        return response;
    }

    @DeleteMapping("/chat")
    @ResponseBody
    public String deleteChatHistory(Authentication authentication) {
        DefaultOAuth2User user = (DefaultOAuth2User) authentication.getPrincipal();
        com.ubetgpt.betgpt.persistence.entity.User user1 = userService.getByUserEmail(user.getAttribute("email")).orElseThrow();
        try {
            chatHistoryService.deleteChatHistoryByUser(user1.getId());
        } catch (Exception e) {
            logger.error("Error deleting chat history: {}", e.getMessage());
            return "failed";
        }
        return "success";
    }
}
