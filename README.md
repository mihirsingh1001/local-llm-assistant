# Local LLM Assistant

A local AI assistant built with **Java, Spring Boot, Spring AI, Qwen 2.5
3B, and llama.cpp**.

The project explores how local Large Language Models (LLMs) can be
integrated into the Java/Spring ecosystem and gradually evolved into a
RAG and Agentic AI application.

## Current Features

-   REST-based chat API
-   Local LLM inference using Qwen 2.5 3B
-   llama.cpp OpenAI-compatible API
-   Spring AI `ChatClient`
-   Conversation memory using `conversationId`
-   Context retention across multiple requests
-   GGUF Q4_K_M quantized model

## Architecture

``` text
Postman / REST Client
        |
        v
Spring Boot REST API :8081
        |
        v
Spring AI ChatClient
        |
        v
MessageChatMemoryAdvisor
        |
        v
Conversation Memory
        |
        v
llama.cpp OpenAI-compatible API :8080
        |
        v
Qwen 2.5 3B Instruct (GGUF / Q4_K_M)
```

## Technology Stack

  Technology             Purpose
  ---------------------- ---------------------------------
  Java 17                Programming language
  Spring Boot 4.1.1      Application framework
  Spring AI 2.0.0        LLM integration
  Spring Web MVC         REST API
  Qwen 2.5 3B Instruct   Local LLM
  GGUF / Q4_K_M          Quantized model
  llama.cpp              Local LLM inference
  Maven                  Build and dependency management
  Postman                API testing
  IntelliJ IDEA          Development

## Local LLM Setup

The project uses **Qwen 2.5 3B Instruct** in **GGUF Q4_K_M** format
through llama.cpp.

Start the local server:

``` powershell
.\llama-server.exe -hf Qwen/Qwen2.5-3B-Instruct-GGUF:Q4_K_M -c 2048 --port 8080
```

The llama.cpp server is available at:

``` text
http://127.0.0.1:8080
```

The Spring Boot application runs on:

``` text
http://localhost:8081
```

## Configuration

Example `application.properties`:

``` properties
spring.application.name=Local-LLM-Assistant
server.port=8081

spring.ai.openai.base-url=http://127.0.0.1:8080
spring.ai.openai.api-key=dummy
spring.ai.openai.chat.options.model=Qwen/Qwen2.5-3B-Instruct-GGUF:Q4_K_M
spring.ai.openai.chat.options.temperature=0.2
spring.ai.openai.chat.max-tokens=150
spring.ai.openai.timeout=300s
spring.ai.openai.max-retries=0
```

The `dummy` API key is used because the local llama.cpp server does not
require a real OpenAI API key.

## REST API

### Chat

``` http
POST /api/chat
```

Full endpoint:

``` text
http://localhost:8081/api/chat
```

Request:

``` json
{
  "conversationId": "chat-1",
  "message": "My name is Mihir"
}
```

Response:

``` json
{
  "message": "My name is Mihir",
  "response": "Hello Mihir! How can I assist you today?"
}
```

## Conversation Memory

Conversation memory is implemented using Spring AI's
`MessageChatMemoryAdvisor`.

A `conversationId` associates multiple requests with the same
conversation.

### Example

First request:

``` json
{
  "conversationId": "chat-1",
  "message": "My name is Mihir"
}
```

Second request:

``` json
{
  "conversationId": "chat-1",
  "message": "What is my name?"
}
```

Because both requests use `chat-1`, the assistant can use the previous
conversation context and answer:

``` text
Your name is Mihir.
```

Using a different ID, such as `chat-2`, creates a separate conversation.

### Current Limitation

The current conversation memory is **in-memory**. Conversation history
is lost when the Spring Boot application restarts.

## Project Structure

``` text
Local-LLM-Assistant/
|
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/mxbx/locallm/
│   │   │       ├── LocalLlmAssistantApplication.java
│   │   │       ├── controller/
│   │   │       │   └── AIController.java
│   │   │       └── dto/
│   │   │           ├── ChatRequestDTO.java
│   │   │           └── ChatResponseDTO.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
|
├── pom.xml
├── mvnw
├── mvnw.cmd
└── README.md
```

## Running the Project

### 1. Start llama.cpp

``` powershell
.\llama-server.exe -hf Qwen/Qwen2.5-3B-Instruct-GGUF:Q4_K_M -c 2048 --port 8080
```

### 2. Start Spring Boot

``` powershell
.\mvnw.cmd spring-boot:run
```

Or run `LocalLlmAssistantApplication` from IntelliJ IDEA.

### 3. Test with Postman

``` text
POST http://localhost:8081/api/chat
```

Body:

``` json
{
  "conversationId": "chat-1",
  "message": "My name is Mihir"
}
```

Then send:

``` json
{
  "conversationId": "chat-1",
  "message": "What is my name?"
}
```

## Future Scope

The project will be developed incrementally toward a complete AI
application.

### 1. Persistent Conversation Memory

Replace in-memory memory with persistent storage, primarily PostgreSQL,
so conversations survive application restarts.

``` text
Spring AI
    |
Chat Memory
    |
PostgreSQL
```

### 2. RAG

Implement Retrieval Augmented Generation:

``` text
Documents
   |
Document Reader
   |
Chunking
   |
Embeddings
   |
Vector Database
   |
Similarity Search
   |
Relevant Context
   |
Qwen 2.5 3B
   |
Answer
```

### 3. Vector Database

Evaluate and integrate a vector store such as:

-   PostgreSQL + pgvector
-   Qdrant
-   Chroma
-   Milvus

### 4. Document-Based Assistant

Add document ingestion for formats such as PDF, TXT, and DOCX, allowing
users to ask questions against their own documents.

### 5. Tool Calling

Allow the LLM to interact with application tools and external services.

Potential tools:

-   Database queries
-   REST APIs
-   Calculator
-   File search
-   Application-specific services

### 6. Agentic AI

Evolve the application from simple question-answering into an agent
capable of planning, selecting tools, executing actions, evaluating
results, and producing a final response.

Target flow:

``` text
User Goal
   |
Agent
   |
Planning
   |
Tool Selection
   |
Tool Execution
   |
Result Evaluation
   |
Additional Actions
   |
Final Response
```

### 7. Streaming Responses

Implement streaming so responses can be displayed progressively instead
of waiting for the complete generation.

### 8. Security

Add Spring Security and JWT authentication so users can have isolated
conversations and protected APIs.

### 9. Frontend

Build a dedicated chat UI using React or Angular.

### 10. Dockerization and Deployment

Containerize the application and supporting services and prepare a
production-oriented deployment architecture.

## Roadmap

### Completed

-   [x] Spring Boot project setup
-   [x] Spring AI integration
-   [x] llama.cpp local LLM setup
-   [x] Qwen 2.5 3B integration
-   [x] GGUF Q4_K_M model
-   [x] OpenAI-compatible local API
-   [x] REST `/api/chat` endpoint
-   [x] Postman testing
-   [x] Conversation memory
-   [x] `conversationId` based context
-   [x] Multi-request conversation test

### Planned

-   [ ] Persistent conversation memory
-   [ ] PostgreSQL integration
-   [ ] RAG
-   [ ] Embeddings
-   [ ] Vector database
-   [ ] Document ingestion
-   [ ] Tool calling
-   [ ] Agentic AI
-   [ ] Streaming responses
-   [ ] Spring Security / JWT
-   [ ] Frontend
-   [ ] Dockerization
-   [ ] Production deployment

## Learning Goals

This project is focused on understanding:

-   Local LLMs and llama.cpp
-   GGUF and quantized models
-   OpenAI-compatible APIs
-   Spring AI and `ChatClient`
-   Conversation memory
-   RAG architecture
-   Embeddings and vector databases
-   Tool calling
-   Agentic AI architecture
-   Production-oriented AI application design with Java and Spring

## Project Vision

The goal is to evolve the application from:

``` text
Question -> LLM -> Answer
```

into:

``` text
LLM
 + Memory
 + RAG
 + Tools
 + Agents
```

while keeping the core AI inference local.

## Author

**Mihirsingh Bais**

Software Developer \| Java \| Spring Boot \| Spring AI \| Generative AI

## License

This project is currently intended for learning, experimentation, and
development purposes.
