package com.quiz.QuizApp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class QuestionDTO {

    private Long id;

    @NotBlank
    private String text;

    @NotEmpty
    private List<AnswerOptionDTO> options;

}
