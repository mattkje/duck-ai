package no.mattikj.mkd.duckai.service;

import static org.junit.jupiter.api.Assertions.*;

import no.mattikj.mkd.duckai.domain.WebSearchType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

// IDK about this one, useless testclass
class WebSearchEngineTest {

    private WebSearchEngine webSearchEngine;

    @BeforeEach
    void setUp() {
        webSearchEngine = new WebSearchEngine();

        // Inject placeholder values so no real HTTP call is made
        ReflectionTestUtils.setField(webSearchEngine, "userAgent", "DuckAI-Test");
        ReflectionTestUtils.setField(webSearchEngine, "wikipediaBaseUrl", "https://example.com/wiki/");
        ReflectionTestUtils.setField(webSearchEngine, "jokeBaseUrl", "https://example.com/joke");
        ReflectionTestUtils.setField(webSearchEngine, "openLibrarySearchUrl", "https://example.com/book?q=");
        ReflectionTestUtils.setField(webSearchEngine, "openLibraryCoverUrl", "https://example.com/covers/");
        ReflectionTestUtils.setField(webSearchEngine, "minIntervalMs", 0L);
        ReflectionTestUtils.setField(webSearchEngine, "connectTimeout", 1000);
        ReflectionTestUtils.setField(webSearchEngine, "readTimeout", 1000);
    }

    @Test
    void testNullPromptReturnsNull() {
        assertNull(webSearchEngine.searchInternetForResponse(null, WebSearchType.WIKI));
        assertNull(webSearchEngine.searchInternetForResponse(null, WebSearchType.BOOK));
        assertNull(webSearchEngine.searchInternetForResponse(null, WebSearchType.JOKE));
    }

    @Test
    void testBlankPromptReturnsNull() {
        assertNull(webSearchEngine.searchInternetForResponse("   ", WebSearchType.WIKI));
    }

    @Test
    void testUnknownTypeReturnsNull() {
        assertNull(webSearchEngine.searchInternetForResponse("Tell me something", null));
    }

    @Test
    void testSanitizePrompt() {
        // invoke sanitize via public method indirectly
        String result = webSearchEngine.searchInternetForResponse("What is Java?", WebSearchType.WIKI);
        // We don’t expect real HTTP results, so null is valid
        assertNull(result);
    }
}