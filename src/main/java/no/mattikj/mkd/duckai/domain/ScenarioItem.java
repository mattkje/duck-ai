package no.mattikj.mkd.duckai.domain;

import java.util.Map;

/**
 * Domain record representing a ScenarioItem.
 *
 * @author Matti Kjellstadli
 * @version 1.2.0
 */
public record ScenarioItem(
    String prompt,
    String response,
    Map<String, Integer> vector
) {
}