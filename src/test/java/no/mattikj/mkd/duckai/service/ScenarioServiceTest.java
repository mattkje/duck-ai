package no.mattikj.mkd.duckai.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import no.mattikj.mkd.duckai.dao.ScenarioDAO;
import no.mattikj.mkd.duckai.domain.Scenario;
import no.mattikj.mkd.duckai.dto.PromptLearnRequest;
import no.mattikj.mkd.duckai.dto.ScenarioDto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ScenarioServiceTest {

    private ScenarioDAO scenarioDAO;
    private ScenarioService scenarioService;

    @BeforeEach
    void setUp() {
        scenarioDAO = mock(ScenarioDAO.class);
        scenarioService = new ScenarioService(scenarioDAO);
    }

    @Test
    void testLoadScenarios() {
        ScenarioDto dto1 = new ScenarioDto();
        dto1.setPrompt("What is a duck?");
        dto1.setAnswer("Only the smartest bird.");

        ScenarioDto dto2 = new ScenarioDto();
        dto2.setPrompt("Hello");
        dto2.setAnswer("Hi there!");

        when(scenarioDAO.getAllScenarios()).thenReturn(List.of(dto1, dto2));

        List<Scenario> scenarios = scenarioService.loadScenarios();
        assertEquals(2, scenarios.size());

        assertEquals("What is a duck?", scenarios.get(0).question());
        assertEquals("Only the smartest bird.", scenarios.get(0).answer());

        assertEquals("Hello", scenarios.get(1).question());
        assertEquals("Hi there!", scenarios.get(1).answer());
    }

    @Test
    void testAddScenarioSuccess() {
        PromptLearnRequest request = new PromptLearnRequest("New prompt?", "New answer!");
        when(scenarioDAO.createScenario(any())).thenReturn(1);

        boolean result = scenarioService.addScenario(request);
        assertTrue(result);

        ArgumentCaptor<ScenarioDto> captor = ArgumentCaptor.forClass(ScenarioDto.class);
        verify(scenarioDAO, times(1)).createScenario(captor.capture());

        ScenarioDto captured = captor.getValue();
        assertEquals("New prompt?", captured.getPrompt());
        assertEquals("New answer!", captured.getAnswer());
    }

    @Test
    void testAddScenarioFailsWhenPromptOrAnswerNull() {
        PromptLearnRequest request1 = new PromptLearnRequest(null, "Answer");
        PromptLearnRequest request2 = new PromptLearnRequest("Prompt", null);

        assertFalse(scenarioService.addScenario(request1));
        assertFalse(scenarioService.addScenario(request2));

        verify(scenarioDAO, never()).createScenario(any());
    }

    @Test
    void testAddScenarioFailsWhenPromptOrAnswerEmpty() {
        PromptLearnRequest request1 = new PromptLearnRequest("   ", "Answer");
        PromptLearnRequest request2 = new PromptLearnRequest("Prompt", "  ");

        assertFalse(scenarioService.addScenario(request1));
        assertFalse(scenarioService.addScenario(request2));

        verify(scenarioDAO, never()).createScenario(any());
    }
}