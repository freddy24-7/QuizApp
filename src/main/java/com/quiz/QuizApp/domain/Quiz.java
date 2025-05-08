package com.quiz.QuizApp.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Quiz {
    @Id
    @GeneratedValue
    private Long id;

    @NotBlank
    private String title;

    private Boolean closed = false;

    @CreationTimestamp
    private LocalDateTime startTime;

    private Integer durationInSeconds;

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL)
    private List<Question> questions = new ArrayList<>();

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL)
    private List<Participant> participants = new ArrayList<>();

    public boolean isClosed() {
        return Boolean.TRUE.equals(closed);
    }

}
