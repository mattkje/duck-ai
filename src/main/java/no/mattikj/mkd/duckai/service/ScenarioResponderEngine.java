package no.mattikj.mkd.duckai.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import no.mattikj.mkd.duckai.domain.Scenario;
import no.mattikj.mkd.duckai.domain.ScenarioItem;
import no.mattikj.mkd.duckai.dto.PromptLearnRequest;

/**
 * ScenarioResponder class that simulates a simple AI with humorous responses.
 * Uses paging to avoid huge memory load and configurable reload interval.
 *
 * @author Matti
 * @version 1.2.0
 */
@Component
@RequiredArgsConstructor
public class ScenarioResponderEngine {

    private static final double SIMILARITY_THRESHOLD = 0.45;
    private final List<ScenarioItem> memory = new CopyOnWriteArrayList<>();
    private final ScenarioService scenarioService;

    private static final Set<String> STOPWORDS = Set.of(
        "the", "is", "a", "an", "and", "or", "what", "how", "are"
    );

    @PostConstruct
    public void init() {
        reloadScenarios();
    }

    @Scheduled(fixedRateString = "${duckai.reload-interval-ms:300000}")
    public void scheduledReload() {
        reloadScenarios();
    }

    public void reloadScenarios() {
        memory.clear();

        List<Scenario> recentScenarios = scenarioService.loadScenarios();

        recentScenarios.forEach(s -> memory.add(
            new ScenarioItem(
                s.question(),
                s.answer(),
                vectorize(s.question())
            )
        ));
    }

    public String generateResponse(String prompt) {
        if (prompt == null || prompt.isBlank()) {
            return "You must speak for me to quack.";
        }

        Map<String, Integer> inputVector = vectorize(prompt);

        ScenarioItem best = findBestMatch(inputVector);

        if (best != null) {
            return best.response();
        }

        return "I have no idea how to respond to that yet.";
    }

    public int learnScenarios(List<PromptLearnRequest> promptLearnRequests) {
        int successCount = 0;
        for (PromptLearnRequest request : promptLearnRequests) {
            if (learn(request)) {
                successCount++;
            }
        }
        return successCount;
    }

    public boolean learn(PromptLearnRequest request) {
        final boolean success = scenarioService.addScenario(request);
        final String prompt = request.getPrompt();
        memory.add(new ScenarioItem(prompt, request.getAnswer(), vectorize(prompt)));
        return success;
    }

    private ScenarioItem findBestMatch(Map<String, Integer> inputVector) {
        double bestScore = 0.0;
        ScenarioItem bestMatch = null;

        for (ScenarioItem item : memory) {
            double score = cosineSimilarity(inputVector, item.vector());
            if (score > bestScore) {
                bestScore = score;
                bestMatch = item;
            }
        }

        return bestScore >= SIMILARITY_THRESHOLD ? bestMatch : null;
    }

    private Map<String, Integer> vectorize(String text) {
        Map<String, Integer> vector = new HashMap<>();

        String[] tokens = text.toLowerCase().split("\\W+");

        for (String token : tokens) {
            if (!token.isBlank() && !STOPWORDS.contains(token)) {
                vector.merge(token, 1, Integer::sum);
            }
        }

        return vector;
    }

    private double cosineSimilarity(Map<String, Integer> v1, Map<String, Integer> v2) {
        Set<String> allKeys = new HashSet<>();
        allKeys.addAll(v1.keySet());
        allKeys.addAll(v2.keySet());

        double dot = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;

        for (String key : allKeys) {
            int a = v1.getOrDefault(key, 0);
            int b = v2.getOrDefault(key, 0);
            dot += a * b;
            norm1 += a * a;
            norm2 += b * b;
        }

        if (norm1 == 0 || norm2 == 0) return 0.0;

        return dot / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }
}