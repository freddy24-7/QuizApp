package com.quiz.QuizApp.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class AnswerOption {
    @Id
    @GeneratedValue
    private Long id;

    @NotBlank
    private String text;

    private boolean correct;

    @ManyToOne
    private Question question;
}
