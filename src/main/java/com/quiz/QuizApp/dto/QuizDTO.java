package com.quiz.QuizApp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class QuizDTO {

    private Long id;

    @NotBlank
    private String title;

    private Integer durationInSeconds;

    private LocalDateTime startTime;

    private Boolean closed;

    @NotEmpty
    private List<QuestionDTO> questions;

    private List<ParticipantDTO> participants;
}
