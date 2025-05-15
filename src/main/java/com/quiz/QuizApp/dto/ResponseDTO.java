package com.quiz.QuizApp.dto;

import lombok.Data;

@Data
public class ResponseDTO {
    private String phoneNumber;
    private String username;
    private Long questionId;
    private String selectedAnswer;
    private Long quizId;
}