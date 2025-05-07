package com.quiz.QuizApp.repository;

import com.quiz.QuizApp.domain.Response;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResponseRepository extends JpaRepository<Response, Long> {

    List<Response> findByParticipant_Quiz_Id(Long quizId);

}
