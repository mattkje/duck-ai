package no.mattikj.mkd.duckai.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
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

import no.mattikj.mkd.duckai.domain.WebSearchType;

/**
 * WebSearchEngine class that fetches summaries from Wikipedia
 * with rate limiting and proper attribution.
 *
 * @author Matti
 * @version 1.2.0
 */
@Component
public class WebSearchEngine {

    @Value("${duckai.user-agent}")
    private String userAgent;

    @Value("${duckai.wikipedia.base-url}")
    private String wikipediaBaseUrl;

    @Value("${duckai.joke.base-url}")
    private String jokeBaseUrl;

    @Value("${duckai.openlibrary.search-url}")
    private String openLibrarySearchUrl;

    @Value("${duckai.openlibrary.cover-url}")
    private String openLibraryCoverUrl;

    @Value("${duckai.rate-limit.ms}")
    private long minIntervalMs;

    @Value("${duckai.http.connect-timeout}")
    private int connectTimeout;

    @Value("${duckai.http.read-timeout}")
    private int readTimeout;

    private static final Set<String> STOPWORDS = new HashSet<>(Arrays.asList(
        "what", "who", "where", "when", "is", "are", "the", "a", "an", "of", "in", "on"
    ));

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final AtomicLong lastRequestTime = new AtomicLong(0);

    /**
     * Returns a Markdown-ready response based on the search type.
     */
    public String searchInternetForResponse(String prompt, WebSearchType type) {
        if (type == null) return null;

        switch (type) {
            case WIKI -> {
                if (prompt == null || prompt.isBlank()) return null;
                String sanitizedPrompt = sanitizePromptForWiki(prompt);
                if (sanitizedPrompt.isBlank()) return null;
                return fetchWikipediaSummaryWithRateLimit(sanitizedPrompt);
            }
            case JOKE -> {
                return fetchJokeFromAPI();
            }
            case BOOK -> {
                if (prompt == null || prompt.isBlank()) return null;
                String sanitizedPrompt = sanitizePromptForWiki(prompt);
                if (sanitizedPrompt.isBlank()) return null;
                return fetchBookFromAPI(sanitizedPrompt);
            }
            default -> {
                return null;
            }
        }
    }

    /**
     * Sanitizes a prompt for Wikipedia-friendly topics (joins words with underscores).
     */
    private String sanitizePromptForWiki(String prompt) {
        String sanitized = prompt.toLowerCase().replaceAll("[^a-zA-Z0-9\\s]", "");
        return Arrays.stream(sanitized.split("\\s+"))
            .filter(w -> !STOPWORDS.contains(w))
            .map(w -> w.isEmpty() ? "" : Character.toUpperCase(w.charAt(0)) + w.substring(1))
            .collect(Collectors.joining("_"));
    }

    /**
     * Fetches Wikipedia summary with rate limiting.
     */
    private String fetchWikipediaSummaryWithRateLimit(String query) {
        long now = System.currentTimeMillis();
        long last = lastRequestTime.get();
        long wait = minIntervalMs - (now - last);
        if (wait > 0) {
            try {
                Thread.sleep(wait);
            } catch (InterruptedException ignored) {
            }
        }

        lastRequestTime.set(System.currentTimeMillis());
        return fetchWikipediaSummary(query);
    }

    private JsonNode getJsonFromUrl(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty("User-Agent", userAgent);
        conn.setConnectTimeout(connectTimeout);
        conn.setReadTimeout(readTimeout);

        try (BufferedReader reader = new BufferedReader(
            new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            return MAPPER.readTree(reader);
        }
    }

    /**
     * Fetches a summary from Wikipedia + appends the source link and attribution.
     */
    private String fetchWikipediaSummary(String query) {
        try {
            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
            String url = wikipediaBaseUrl + encodedQuery;

            JsonNode root = getJsonFromUrl(url);

            String extract = root.path("extract").asText("");
            if (extract.isBlank()) return null;

            String imageUrl = root.path("originalimage").path("source").asText("");
            String pageUrl = root.path("content_urls").path("desktop").path("page").asText("");

            StringBuilder result = new StringBuilder(extract);

            if (!imageUrl.isBlank()) {
                result.append("<br>![").append(query).append("](").append(imageUrl).append(")");
            }

            if (!pageUrl.isBlank()) {
                result.append("<br>Source: [Wikipedia Article](").append(pageUrl).append(")");
            }

            result.append("<br>*(Information from Wikipedia, CC BY-SA 3.0)*");

            return result.toString();

        } catch (Exception ignored) {
            return null;
        }
    }

    private String fetchJokeFromAPI() {
        try {
            JsonNode root = getJsonFromUrl(jokeBaseUrl);

            if (root.path("type").asText().equals("single")) {
                return root.path("joke").asText(null);
            }

            if (root.path("type").asText().equals("twopart")) {
                return root.path("setup").asText("") +
                       "<br>" +
                       root.path("delivery").asText("");
            }

        } catch (Exception ignored) {
        }

        return null;
    }

    private String fetchBookFromAPI(String query) {
        try {
            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
            String url = openLibrarySearchUrl + encodedQuery + "&limit=1";

            JsonNode root = getJsonFromUrl(url);
            JsonNode docs = root.path("docs");

            if (!docs.isArray() || docs.isEmpty()) return null;

            JsonNode book = docs.get(0);

            String title = book.path("title").asText("");
            String author = book.path("author_name").isArray()
                ? book.path("author_name").get(0).asText("")
                : "";
            String year = book.path("first_publish_year").asText("");
            String workKey = book.path("key").asText("");
            String coverId = book.path("cover_i").asText("");

            if (title.isBlank()) return null;

            StringBuilder result = new StringBuilder();

            result.append("### ").append(title);

            if (!author.isBlank()) {
                result.append("<br>**Author:** ").append(author);
            }

            if (!year.isBlank()) {
                result.append("<br>**First Published:** ").append(year);
            }

            if (!coverId.isBlank()) {
                String coverUrl = openLibraryCoverUrl + coverId + "-L.jpg";
                result.append("<br>![").append(title).append(" Cover](")
                    .append(coverUrl).append(")");
            }

            if (!workKey.isBlank()) {
                result.append("<br>Source: [Open Library](https://openlibrary.org")
                    .append(workKey).append(")");
            }

            result.append("<br>*(Information from Open Library — Free & Open API)*");

            return result.toString();

        } catch (Exception ignored) {
            return null;
        }
    }
}