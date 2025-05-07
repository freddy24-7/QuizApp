package com.quiz.QuizApp.websocket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnswerSubmission {
    private Long quizId;
    private Long questionId;
    private String selectedAnswer;
    private String playerId;
    private String username;

}
