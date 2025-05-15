package com.quiz.QuizApp.repository;

import com.quiz.QuizApp.domain.Participant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {
    Optional<Participant> findByPhoneNumber(String phoneNumber);

    void deleteAllByQuiz_Id(Long quizId);

    List<Participant> findByQuiz_Id(Long quizId);
}
