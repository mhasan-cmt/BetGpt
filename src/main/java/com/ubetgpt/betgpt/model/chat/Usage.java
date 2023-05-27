package com.ubetgpt.betgpt.model.chat;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Usage {
    public int prompt_tokens;
    public int completion_tokens;
    public int total_tokens;
}
