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

    @NotNull(message = "Duration in seconds is required")  // Ensure duration is not null
    private Integer durationInSeconds;

    @NotNull(message = "Start time is required")  // Ensure start time is not null
    private LocalDateTime startTime;

    private Boolean closed = false;  // Default to false if not provided

    @NotEmpty(message = "Quiz must contain at least one question")  // Ensure questions are not empty
    private List<QuestionDTO> questions;

    private List<ParticipantDTO> participants;  // This can be empty, so no validation required

}
