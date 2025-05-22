package com.quiz.QuizApp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class QuizDTO {

    private Long id;

    @NotBlank(message = "Title is required")
    private String title;

    @NotNull(message = "Duration in seconds is required")
    private Integer durationInSeconds;

    @NotNull(message = "Start time is required")
    private LocalDateTime startTime;

    private Boolean closed = false;

    @NotEmpty(message = "Quiz must contain at least one question")
    private List<QuestionDTO> questions;

    private List<ParticipantDTO> participants;

}
