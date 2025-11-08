package no.mattikj.mkd.duckai.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO representing a response from the DuckAI service.
 *
 * @author Matti Kjellstadli
 * @version 1.1.0
 */
@Setter
@Getter
public class PromptResponse {
    private String reply;

    public PromptResponse() {}

    public PromptResponse(String reply) {
        this.reply = reply;
    }
}