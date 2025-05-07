package com.quiz.QuizApp.dto;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ParticipantDTO {

    private Long id;

    @Pattern(regexp = "\\+?[0-9]{10,15}")
    private String phoneNumber;
}
