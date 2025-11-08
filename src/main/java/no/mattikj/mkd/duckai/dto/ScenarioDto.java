package no.mattikj.mkd.duckai.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ScenarioDto {
    private String scenarioId;
    private String prompt;
    private String answer;

    public ScenarioDto() {
    }

    public ScenarioDto(final Long scenarioId, final String prompt, final String answer) {
        this.scenarioId = String.valueOf(scenarioId);
        this.prompt = prompt;
        this.answer = answer;
    }
}
