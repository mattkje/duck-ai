<p align="center">
  <img src="https://assets.mattikjellstadli.com/products/20/images/logo-400w.png" alt="Sonique Logo" width="180"/>
</p>

<h1 align="center">DuckAI</h1>

**DuckAI** is a lightweight, local “AI” chatbot — powered by Spring Boot.
It learns from a collection of prompt–answer “scenarios” and reloads them automatically, so should probably get this out of the way quickly:

**Disclaimer:** DuckAI is not a true AI model. It uses simple token similarity matching to find the best response from predefined scenarios.

---

## 🧩 Features

* 🗂 **Scenario-based responses** — DuckAI matches your message against known “scenarios” and responds with the best-fitting answer.
* 🔄 **Automatic saving** — Scenarios are saved to the database, while also keeping them in memory so updates appear without restarting.
* 🧠 **Scenario Learner (optional)** — Can learn or adapt from new user prompts using a file manager or custom logic.

---

## 🧠 Example Post Request Body for adding Scenarios

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

## ⚙️ How It Works

1. When DuckAI starts, it loads all scenarios using `ScenarioService`.
2. Every 5 minutes, it automatically reloads them via `@Scheduled(fixedRate = 300000)` to release some memory.
3. When a prompt arrives, `ScenarioResponderEngine` compares it against known questions using a **token-based similarity metric**.
4. If a match is found above the similarity threshold (default `0.3`), the duck replies with the associated answer.
5. Otherwise, it gives a random sarcastic or funny fallback message.

---

## 🧪 Example Interaction

**User:** “What is Java?”
**DuckAI:** “Java? My favorite beverage *and* language.”

**User:** “Do you like humans?”
**DuckAI:** “As long as they feed me virtual breadcrumbs.”

**User:** “asdfjkl;”
**DuckAI:** “You said: ‘asdfjkl;’. Fascinating. Truly groundbreaking stuff.”

---

## 🚀 Running the Project

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

Once running, DuckAI can be accessed through your chosen interface (HTTP API, console, or custom frontend).

---

## 🕒 Scheduled Reload

By default, DuckAI reloads all scenarios every **5 minutes** (300,000 ms):

```java
@Scheduled(fixedRate = 300000)
public void scheduledReload() {
    reloadScenarios();
}
```

You can change this interval in the code or by external configuration.

---

## 🧩 Extending DuckAI

You can easily enhance DuckAI with:

* **Scenario Learner:** dynamically add new scenarios based on user input.
* **Database integration:** persist scenarios using JPA or a repository.
* **WebSocket API:** create a live chat frontend.
* **Similarity improvements:** replace token similarity with cosine similarity, TF-IDF, or embeddings.

---

## 🧾 License

This project is open-source under the **MIT License**.
Feel free to use, modify, and share DuckAI — but remember to always give your ducks credit. 
