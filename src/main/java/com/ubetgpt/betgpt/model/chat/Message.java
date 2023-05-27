package com.ubetgpt.betgpt.model.chat;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Message {
    public String role;
    public String content;
}
