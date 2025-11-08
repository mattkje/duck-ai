package no.mattikj.mkd.duckai.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO representing a request to the DuckAI service.
 *
 * @author Matti Kjellstadli
 * @version 1.1.0
 */
@Setter
@Getter
public class PromptRequest {
    private String prompt;

    public PromptRequest() {
    }

    public PromptRequest(String prompt) {
        this.prompt = prompt;
    }
}