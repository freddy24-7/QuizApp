package com.quiz.QuizApp.dto;

import lombok.Data;

@Data
public class QuizSummaryDTO {
    private Long id;
    private String title;
    private int questionCount;
    private int participantCount;
}
