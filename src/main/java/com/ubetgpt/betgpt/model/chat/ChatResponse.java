package com.ubetgpt.betgpt.model.chat;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
@Data
@Builder
@ToString
public class ChatResponse {
    public String id;
    public String object;
    public int created;
    public String model;
    public Usage usage;
    public ArrayList<Choice> choices;
}
