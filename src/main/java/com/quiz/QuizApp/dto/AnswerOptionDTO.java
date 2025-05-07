package com.quiz.QuizApp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AnswerOptionDTO {

    private Long id;

    @NotBlank
    private String text;

    private boolean correct;
}
