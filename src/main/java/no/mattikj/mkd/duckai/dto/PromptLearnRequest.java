package no.mattikj.mkd.duckai.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO representing a learning request to the DuckAI service.
 *
 * @author Matti Kjellstadli
 * @version 1.1.0
 */
@Setter
@Getter
public class PromptLearnRequest {
    private String prompt;
    private String answer;

    public PromptLearnRequest() {
    }

    public PromptLearnRequest(final String prompt, final String answer) {
        this.prompt = prompt;
        this.answer = answer;
    }
}