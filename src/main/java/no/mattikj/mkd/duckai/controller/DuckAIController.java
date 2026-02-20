package no.mattikj.mkd.duckai.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import no.mattikj.mkd.duckai.domain.ResponseSourceType;
import no.mattikj.mkd.duckai.dto.PromptLearnRequest;
import no.mattikj.mkd.duckai.dto.PromptRequest;
import no.mattikj.mkd.duckai.dto.PromptResponse;
import no.mattikj.mkd.duckai.service.ScenarioResponderEngine;
import no.mattikj.mkd.duckai.service.ScenarioService;

/**
 * DuckAI Controller class.
 *
 * @author Matti Kjellstadli
 * @version 1.2.0
 */
@RestController
@RequestMapping("/api/duckai")
@RequiredArgsConstructor
public class DuckAIController {

    private final ScenarioResponderEngine scenarioResponderEngine;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public PromptResponse handleDuckAIRequest(@RequestBody final PromptRequest request) {
        return scenarioResponderEngine.generateResponse(request.getPrompt());
    }

    @PostMapping("/learn")
    public PromptResponse handleDuckAILearning(@RequestBody final List<PromptLearnRequest> promptLearnRequests) {
        final int successCount = scenarioResponderEngine.learnScenarios(promptLearnRequests);
        return new PromptResponse("Successfully learned " + successCount + " scenarios.", ResponseSourceType.LOCAL);
    }
}