package com.quiz.QuizApp.repository;

import com.quiz.QuizApp.domain.Response;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ResponseRepository extends JpaRepository<Response, Long> {

    List<Response> findByParticipant_Quiz_Id(Long quizId);

    Optional<Response> findByParticipant_IdAndQuestion_Id(Long participantId, Long questionId);

}
