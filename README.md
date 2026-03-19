# AI Moderation Client

A reusable Spring Boot client library for AI-powered content moderation and prompt validation. This library provides a simple way for other Spring Boot applications to integrate AI moderation services.

## Features

- Integrates with Spring AI for moderation capabilities.
- Pre-configured HTTP client using OkHttp.
- Seamless JSON processing with Jackson.
- Designed as a lightweight library to be imported into any Spring Boot 3.x application.

## Prerequisites

- **Java:** 17 or higher
- **Maven:** 3.8+

## How to Build the Library

To use this project as a dependency in other applications, you first need to build it and install it into your local Maven repository (`~/.m2/repository`).

From the root directory of this project, run the following Maven command:

```bash
  mvn clean install
```

This command will compile the code, run tests, and package it into a standard JAR file. It will then install this JAR into your local Maven repository, making it available to your other local projects.

## How to Use in Another Application

Once the library is installed locally (or deployed to a remote artifact repository like Nexus or Artifactory), you can include it in any other Maven-based Spring Boot project.

### 1. Add the Maven Dependency

Add the following `<dependency>` block to your target application's `pom.xml` file:

```xml
<dependency>
    <groupId>com.subhashish</groupId>
    <artifactId>ai-moderation-client</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

### 2. Call validate method

Once the dependency is added, you can inject the provided `ModerationValidator` into your Spring components and call the `validate` method with the content you want to check.

### 3. Configuration Properties

To configure the library, you may need to provide your AI provider credentials or override default settings in your target application's `application.properties` or `application.yml` file:

```yaml
gemini-model-url= GEMINI_MODEL_URL
gemini-api-key=YOUR_API_KEY
```
