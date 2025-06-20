# ktor-ideas

A Kotlin-based RESTful API project built with Ktor framework, demonstrating clean architecture principles and dependency injection with Koin.

## Project Overview

This project implements a simple Person Management API with the following features:
- Create and retrieve person records
- Health check endpoint
- Clean architecture with separation of concerns
- Dependency injection using Koin
- Database persistence with Exposed ORM

This project was created using the [Ktor Project Generator](https://start.ktor.io).

Here are some useful links to get you started:

- [Ktor Documentation](https://ktor.io/docs/home.html)
- [Ktor GitHub page](https://github.com/ktorio/ktor)
- The [Ktor Slack chat](https://app.slack.com/client/T09229ZC6/C0A974TJ9). You'll need
  to [request an invite](https://surveys.jetbrains.com/s3/kotlin-slack-sign-up) to join.

## Project Structure

```
src/main/kotlin/com/fsociety/ktor/ideas/
├── Application.kt                    # Main application entry point
├── common/                           # Common DTOs and utilities
│   ├── request/                      # Request models
│   └── response/                     # Response models
├── domain/                           # Domain layer
│   ├── db/                           # Database configuration and entities
│   ├── model/                        # Domain models
│   └── repository/                   # Repository interfaces and implementations
├── http/                             # HTTP layer
│   ├── controller/                   # Controllers handling business logic
│   └── route/                        # Route definitions
├── plugins/                          # Ktor plugins configuration
└── service/                          # Service layer
```

## API Endpoints

- `GET /user/` - User Management API information
- `POST /user` - Create a new person
- `GET /user/all` - Get all persons
- `GET /health` - Health check endpoint

## Features

Here's a list of features included in this project:

| Name                                                                   | Description                                                                        |
|------------------------------------------------------------------------|------------------------------------------------------------------------------------|
| [Routing](https://start.ktor.io/p/routing)                             | Provides a structured routing DSL                                                  |
| [Content Negotiation](https://start.ktor.io/p/content-negotiation)     | Provides automatic content conversion according to Content-Type and Accept headers |
| [kotlinx.serialization](https://start.ktor.io/p/kotlinx-serialization) | Handles JSON serialization using kotlinx.serialization library                     |
| [Exposed](https://start.ktor.io/p/exposed)                             | Adds Exposed database to your application                                          |
| [CORS](https://start.ktor.io/p/cors)                                   | Enables Cross-Origin Resource Sharing (CORS)                                       |

## Building & Running

To build or run the project, use one of the following tasks:

| Task                          | Description                                                          |
|-------------------------------|----------------------------------------------------------------------|
| `./gradlew test`              | Run the tests                                                        |
| `./gradlew build`             | Build everything                                                     |
| `buildFatJar`                 | Build an executable JAR of the server with all dependencies included |
| `buildImage`                  | Build the docker image to use with the fat JAR                       |
| `publishImageToLocalRegistry` | Publish the docker image locally                                     |
| `run`                         | Run the server                                                       |
| `runDocker`                   | Run using the local docker image                                     |

If the server starts successfully, you'll see the following output:

```
2024-12-04 14:32:45.584 [main] INFO  Application - Application started in 0.303 seconds.
2024-12-04 14:32:45.682 [main] INFO  Application - Responding at http://0.0.0.0:8080
```
