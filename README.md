🧠 QuizApp - Backend

QuizApp is a Spring Boot-based backend application that manages online quizzes. 
It supports quiz creation, user participation, real-time progress tracking, 
and result aggregation.

📦 Features

* Create quizzes with questions and answer options
* Add participants by phone number
* Record and update user responses
* Automatically close quizzes after timeout
* Track quiz progress (per user)
* View quiz results with scores and metadata
* RESTful API for frontend integration

⚙️ Tech Stack
* Java 17+
* Spring Boot
* Spring Data JPA (Hibernate)
* H2/PostgreSQL (configurable)
* Lombok
* Maven

🚀 Getting Started

Clone the Repository:
1. git clone https://github.com/freddy24-7/QuizApp
2. cd Quizapp

3. Build the Project:
* mvn clean install

4. Run the application

* App will be available at:
http://localhost:8080

🛠️ Configuration
* Edit application.properties, to switch from default
* Dev mode is default
* Switch to PostgreSQL or MySQL for production

📚 API Overview 

Quiz Management
* POST /api/quizzes — Create new quiz
* GET /api/quizzes/{id} — Get quiz details
* DELETE /api/quizzes/{id} — Delete quiz

Responses
* POST /api/responses — Submit or update answer
* GET /api/responses/results/{quizId} — Get aggregated results
* GET /api/responses/progress/{quizId} — Track live user progress
* DELETE /api/responses/reset/{quizId} — Reset all responses for a quiz

Sample Payload (POST /api/quizzes)
```json
{
  "title": "General Knowledge Quiz",
  "durationInSeconds": 600,
  "startTime": "2025-05-15T10:00:00",
  "closed": false,
  "questions": [
    {
      "text": "What is the capital of France?",
      "options": [
        { "text": "Paris", "correct": true },
        { "text": "London", "correct": false },
        { "text": "Berlin", "correct": false },
        { "text": "Madrid", "correct": false }
      ]
    }
  ],
  "participants": [
    { "phoneNumber": "0612345678" }
  ]
}
