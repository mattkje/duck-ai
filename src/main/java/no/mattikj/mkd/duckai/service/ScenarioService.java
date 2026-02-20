package no.mattikj.mkd.duckai.service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import no.mattikj.mkd.duckai.dao.ScenarioDAO;
import no.mattikj.mkd.duckai.domain.Scenario;
import no.mattikj.mkd.duckai.dto.PromptLearnRequest;
import no.mattikj.mkd.duckai.dto.ScenarioDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Handles reading, writing, and training scenario (question → answer) data using the database instead of a file.
 *
 * @author Matti Kjellstadli
 * @version 1.1.0
 */
@Service
@RequiredArgsConstructor
public class ScenarioService {

    private static final Logger LOG = LoggerFactory.getLogger(ScenarioService.class);
    private final ScenarioDAO scenarioDAO;

    public List<Scenario> loadScenarios() {
        List<ScenarioDto> dtos = scenarioDAO.getAllScenarios();
        return dtos.stream().map(dto -> new Scenario(dto.getPrompt(), dto.getAnswer())).collect(Collectors.toList());
    }

    public boolean addScenario(PromptLearnRequest promptLearnRequest) {
        String prompt = promptLearnRequest.getPrompt();
        String answer = promptLearnRequest.getAnswer();
        if (prompt == null || answer == null) {
            LOG.warn("Prompt or answer is null. Prompt: {}, Answer: {}", prompt, answer);
            return false;
        }

        prompt = prompt.trim();
        answer = answer.trim();
        if (prompt.isEmpty() || answer.isEmpty()) {
            LOG.warn("Prompt or answer is empty after trimming. Prompt: '{}', Answer: '{}'", prompt, answer);
            return false;
        }

        ScenarioDto dto = new ScenarioDto();
        dto.setPrompt(prompt);
        dto.setAnswer(answer);

        int rows = scenarioDAO.createScenario(dto);
        return rows > 0;
    }
}
