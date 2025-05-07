package com.quiz.QuizApp.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Response {
    @Id
    @GeneratedValue
    private Long id;

    private String username;

    @ManyToOne
    private Participant participant;

    @ManyToOne
    private Question question;

    private String selectedAnswer;
}