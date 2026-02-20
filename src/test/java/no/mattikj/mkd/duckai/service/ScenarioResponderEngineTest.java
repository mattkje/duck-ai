package no.mattikj.mkd.duckai.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;

import java.util.List;

import no.mattikj.mkd.duckai.domain.ResponseSourceType;
import no.mattikj.mkd.duckai.domain.Scenario;
import no.mattikj.mkd.duckai.domain.WebSearchType;
import no.mattikj.mkd.duckai.dto.PromptLearnRequest;
import no.mattikj.mkd.duckai.dto.PromptResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ScenarioResponderEngineTest {

    private ScenarioService scenarioService;
    private WebSearchEngine webSearchEngine;
    private ScenarioResponderEngine engine;

    @BeforeEach
    void setUp() {
        scenarioService = mock(ScenarioService.class);
        webSearchEngine = mock(WebSearchEngine.class);
        engine = new ScenarioResponderEngine(scenarioService, webSearchEngine);

        // Mock scenarioService to return some predefined scenarios
        when(scenarioService.loadScenarios()).thenReturn(List.of(
            new Scenario("What is a duck?", "Only the smartest bird."),
            new Scenario("Hello", "Hi there!")
        ));

        // Initialize engine (calls reloadScenarios internally)
        engine.init();
    }

    @Test
    void testGenerateResponseWithCustomScenario() {
        PromptResponse response = engine.generateResponse("What is a duck?");
        assertNotNull(response);
        assertEquals(ResponseSourceType.LOCAL, response.getSource());
        assertEquals("Only the smartest bird.", response.getReply());
    }

    @Test
    void testGenerateResponseWithNoMatchFallsBackToWeb() {
        // Mock the webSearchEngine to return a summary
        when(webSearchEngine.searchInternetForResponse("Who was Alan Turing?", WebSearchType.WIKI))
            .thenReturn("Alan Turing was a mathematician.");

        PromptResponse response = engine.generateResponse("Who was Alan Turing?");
        assertNotNull(response);
        assertEquals(ResponseSourceType.INTERNET, response.getSource());
        assertEquals("Alan Turing was a mathematician.", response.getReply());
    }

    @Test
    void testGenerateResponseWithEmptyPrompt() {
        PromptResponse response = engine.generateResponse("");
        assertNotNull(response);
        assertEquals(ResponseSourceType.LOCAL, response.getSource());
        assertEquals("You must speak for me to quack.", response.getReply());
    }

    @Test
    void testClassifyPrompt() {
        // JOKE
        assertEquals(WebSearchType.JOKE, engine.classifyPrompt("Tell me a funny joke."));

        // BOOK
        assertEquals(WebSearchType.BOOK, engine.classifyPrompt("I want a book recommendation."));

        // OTHER
        assertEquals(WebSearchType.OTHER, engine.classifyPrompt("Explain gravity."));
    }

    @Test
    void testLearnScenario() {
        PromptLearnRequest request = new PromptLearnRequest("New prompt?", "New answer!");
        when(scenarioService.addScenario(ArgumentMatchers.any())).thenReturn(true);

        boolean success = engine.learn(request);
        assertTrue(success);

        // Now it should match locally
        PromptResponse response = engine.generateResponse("New prompt?");
        assertEquals("New answer!", response.getReply());
        assertEquals(ResponseSourceType.LOCAL, response.getSource());
    }
}