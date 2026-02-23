package no.mattikj.mkd.duckai.dto;

import lombok.Getter;
import lombok.Setter;
import no.mattikj.mkd.duckai.domain.ResponseSourceType;

/**
 * DTO representing a response from the DuckAI service.
 *
 * @author Matti Kjellstadli
 * @version 1.2.0
 */
@Setter
@Getter
public class PromptResponse {
    private String reply;
    private ResponseSourceType source;

    public PromptResponse() {
    }

    public PromptResponse(final String reply, final ResponseSourceType source) {
        this.reply = reply;
        this.source = source;
    }
}