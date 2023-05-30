package com.ubetgpt.betgpt.model.chat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Choice {
    public Message message;
    public String finish_reason;
    public int index;
}
