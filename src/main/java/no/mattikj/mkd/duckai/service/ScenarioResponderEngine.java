package no.mattikj.mkd.duckai.service;

import jakarta.annotation.PostConstruct;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;
import lombok.RequiredArgsConstructor;
import no.mattikj.mkd.duckai.domain.Scenario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * ScenarioResponder class that simulates a simple AI with humorous responses.
 *
 * @author Matti
 * @version 1.1.0
 */
@Component
@RequiredArgsConstructor
public class ScenarioResponderEngine {

    private static final Logger LOG = LoggerFactory.getLogger(ScenarioResponderEngine.class);
    private final List<String> history = new CopyOnWriteArrayList<>();
    private final Random random = ThreadLocalRandom.current();
    private final ScenarioService scenarioService;

    private static final double SIMILARITY_THRESHOLD = 0.3;

    private List<Scenario> scenarios;

    @PostConstruct
    public void init() {
        reloadScenarios();
    }

    @Scheduled(fixedRate = 300000)
    public void scheduledReload() {
        reloadScenarios();
    }

    public String generateResponse(final String prompt) {
        if (prompt == null || prompt.trim().isEmpty()) {
            return "Quack? You said nothing. Are you okay?";
        }

        String trimmed = prompt.trim();
        history.add(trimmed);
        if (history.size() > 200) {
            history.remove(0);
        }

        Scenario best = findBestMatch(trimmed);
        if (best != null) {
            return best.answer();
        }

        return fallbackReply(trimmed);
    }

    private Scenario findBestMatch(String prompt) {
        double bestScore = 0.0;
        Scenario bestScenario = null;
        for (Scenario s : scenarios) {
            double score = similarity(prompt.toLowerCase(), s.question().toLowerCase());
            if (score > bestScore) {
                bestScore = score;
                bestScenario = s;
            }
        }

        if (bestScore >= SIMILARITY_THRESHOLD) {
            return bestScenario;
        }
        return null;
    }

    private double similarity(String a, String b) {
        Set<String> tokensA = new HashSet<>(Arrays.asList(a.split("\\s+")));
        Set<String> tokensB = new HashSet<>(Arrays.asList(b.split("\\s+")));
        if (tokensA.isEmpty() || tokensB.isEmpty()) {
            return 0.0;
        }

        Set<String> intersection = new HashSet<>(tokensA);
        intersection.retainAll(tokensB);

        Set<String> union = new HashSet<>(tokensA);
        union.addAll(tokensB);

        return (double) intersection.size() / union.size();
    }

    private String fallbackReply(String prompt) {
        String[] endings = {
            "Fascinating. Truly groundbreaking stuff.",
            "Are you sure about that?",
            "Wow. Incredible. I'm totally processing that correctly.",
            "Quack. (That’s duck for 'whatever').",
            "I’ll add that to my list of things to ignore.",
            "Let me just consult my imaginary friend on that one.",
            "Sounds important. I'll pretend to care."};
        return "You said: \"" + prompt + "\". " + endings[random.nextInt(endings.length)];
    }

    public void reloadScenarios() {
        this.scenarios = scenarioService.loadScenarios();
        LOG.info("Reloaded {} scenarios.", scenarios.size());
    }
}
