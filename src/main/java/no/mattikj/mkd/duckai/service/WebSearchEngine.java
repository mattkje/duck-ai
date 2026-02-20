package no.mattikj.mkd.duckai.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * WebSearchEngine class that fetches summaries from Wikipedia
 * with rate limiting and proper attribution.
 *
 * @author Matti
 * @version 1.2.0
 */
@Component
public class WebSearchEngine {

    private static final Set<String> STOPWORDS = new HashSet<>(Arrays.asList(
        "what", "who", "where", "when", "is", "are", "the", "a", "an", "of", "in", "on"
    ));

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final long MIN_INTERVAL_MS = 1000;
    private final AtomicLong lastRequestTime = new AtomicLong(0);

    public String searchInternetForResponse(String prompt) {
        if (prompt == null || prompt.isBlank()) {
            return null;
        }

        String sanitizedPrompt = sanitizePromptForWiki(prompt);
        if (sanitizedPrompt.isBlank()) {
            return null;
        }

        return fetchWikipediaSummaryWithRateLimit(sanitizedPrompt);
    }

    private String sanitizePromptForWiki(String prompt) {
        String sanitized = prompt.toLowerCase()
            .replaceAll("[^a-zA-Z0-9\\s]", "");

        return Arrays.stream(sanitized.split("\\s+"))
            .filter(w -> !STOPWORDS.contains(w))
            .map(w -> w.isEmpty() ? "" : Character.toUpperCase(w.charAt(0)) + w.substring(1))
            .collect(Collectors.joining(" "));
    }

    /**
     * Fetches Wikipedia summary with rate limiting.
     */
    private String fetchWikipediaSummaryWithRateLimit(String query) {
        long now = System.currentTimeMillis();
        long last = lastRequestTime.get();
        long wait = MIN_INTERVAL_MS - (now - last);
        if (wait > 0) {
            try {
                Thread.sleep(wait);
            } catch (InterruptedException ignored) {
            }
        }

        lastRequestTime.set(System.currentTimeMillis());
        return fetchWikipediaSummary(query);
    }

    /**
     * Fetches a summary from Wikipedia + appends the source link and attribution.
     */
    private String fetchWikipediaSummary(String query) {
        String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
        String urlString = "https://en.wikipedia.org/api/rest_v1/page/summary/" + encodedQuery;

        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("User-Agent", "DuckAI/2.1 (contact@example.com)");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                JsonNode root = MAPPER.readTree(reader);
                JsonNode extract = root.get("extract");
                JsonNode pageUrl = root.path("content_urls").path("desktop").path("page");

                if (extract != null && !extract.asText().isBlank()) {
                    String result = extract.asText();
                    if (pageUrl != null && !pageUrl.asText().isBlank()) {
                        result += "\n\nSource: " + pageUrl.asText();
                    }
                    result += "\n\n(Information from Wikipedia, CC BY-SA 3.0)";
                    return result;
                }
            }

        } catch (Exception e) {
            System.err.println("Error fetching Wikipedia summary: " + e.getMessage());
        }

        return null;
    }
}