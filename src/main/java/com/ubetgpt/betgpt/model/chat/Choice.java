package com.ubetgpt.betgpt.model.chat;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Choice {
    public Message message;
    public String finish_reason;
    public int index;
}
