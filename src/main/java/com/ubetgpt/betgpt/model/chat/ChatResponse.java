package com.ubetgpt.betgpt.model.chat;

import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
@Data
@ToString
public class ChatResponse {
    public String id;
    public String object;
    public int created;
    public String model;
    public Usage usage;
    public ArrayList<Choice> choices;
}
