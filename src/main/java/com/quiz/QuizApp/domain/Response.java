package com.quiz.QuizApp.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Response {
    @Id
    @GeneratedValue
    private Long id;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    private String username;

    @ManyToOne
    private Participant participant;

    @ManyToOne
    private Question question;

    private String selectedAnswer;
}