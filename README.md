<p align="center">
  <img src="https://assets.mattikjellstadli.com/products/20/images/logo-400w.png" alt="DuckAI Logo" width="180"/>
</p>

<h1 align="center">DuckAI</h1>

**DuckAI** is a lightweight, local chatbot framework powered by Spring Boot.
It learns from a collection of prompt–answer scenarios and can dynamically reload them without restarting.

**Disclaimer:**
DuckAI is not a general-purpose AI model. It relies on token similarity and classification logic to determine responses.
External knowledge is fetched from public APIs only when no custom scenario matches.

---

## Features

* **Scenario-based responses** — Matches prompts against stored scenarios using cosine similarity.
* **Custom prompt prioritization** — Learned scenarios are always checked before external APIs.
* **Wikipedia fallback** — Retrieves article summaries and images when no custom match is found.
* **Book search (Open Library)** — Fetches book details and cover images without requiring API keys.
* **Joke API integration** — Returns random jokes on demand.
* **Automatic scenario reloading** — Scenarios refresh periodically without restarting the service.
* **Rate-limited API requests** — Prevents excessive queries to external services.
* **Markdown-ready responses** — All external responses include Markdown formatting for links and images.

---

## Example Scenario Request

```json
[
  {
    "prompt": "What is a duck",
    "answer": "Uhhh, only the most intelligent species on earth?"
  },
  {
    "prompt": "Help",
    "answer": "Quack! I’m here to assist. Try asking me anything, human."
  }
]
```

---

## Architecture & Workflow

1. **Startup:** DuckAI loads all predefined scenarios into memory.
2. **Scheduled Reload:** Every 5 minutes, scenarios are refreshed automatically via `@Scheduled`.
3. **Prompt Handling:**

   * Explicit intents (e.g., joke, book) are processed immediately.
   * Otherwise, custom scenarios are evaluated using cosine similarity.
   * Wikipedia is used as a fallback if no scenario matches sufficiently.
4. **Response Formatting:** All external responses include Markdown-formatted links and images.
5. **Fallback:** If no suitable response is found, DuckAI returns a generic fallback message.

---

## External Data Sources

DuckAI integrates with the following free, public APIs (no API keys required):

| Source                 | Description                                                   |
| ---------------------- | ------------------------------------------------------------- |
| **Wikipedia REST API** | Retrieves article summaries and images.                       |
| **Open Library API**   | Fetches book metadata, authors, publication year, and covers. |
| **JokeAPI**            | Returns random jokes.                                         |

All external requests include a proper `User-Agent` and are rate-limited.

---

## Example Interaction

**User:** “What is Java?”
**DuckAI:** “Java? My favorite beverage *and* language.”

**User:** “Do you like humans?”
**DuckAI:** “As long as they feed me virtual breadcrumbs.”

**User:** “asdfjkl;”
**DuckAI:** “You said: ‘asdfjkl;’. Fascinating. Truly groundbreaking stuff.”

---

## Running the Project

### Prerequisites

* Java 21+
* Maven or Gradle

### Run via Maven

```bash
mvn spring-boot:run
```

### Or build and run

```bash
mvn clean package
java -jar target/duckai-*.jar
```

Once running, DuckAI can be accessed via HTTP API, console, or a custom frontend.

---

## Scheduled Reload

By default, DuckAI reloads all scenarios every **5 minutes (300,000 ms)**:

```java
@Scheduled(fixedRate = 300000)
public void scheduledReload() {
    reloadScenarios();
}
```

The interval can be customized via external configuration.

---

## Extending DuckAI

DuckAI is designed for extensibility:

* **Scenario Learner:** Dynamically add new scenarios based on user input.
* **Database integration:** Persist scenarios using JPA or a repository.
* **WebSocket API:** Enable real-time chat frontends.
* **Similarity enhancements:** Upgrade token similarity with TF-IDF, cosine similarity, or embeddings for smarter responses.

---

## License

This project is open-source under the **MIT License**.
Feel free to use, modify, and share DuckAI — but please give credit where due.