package com.quiz.QuizApp.repository;

import com.quiz.QuizApp.domain.Response;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ResponseRepository extends JpaRepository<Response, Long> {

    List<Response> findByParticipant_Quiz_Id(Long quizId);

    Optional<Response> findByParticipant_IdAndQuestion_Id(Long participantId, Long questionId);

    @Query("SELECT r FROM Response r " +
            "JOIN FETCH r.question q " +
            "JOIN FETCH q.options " +
            "WHERE r.quiz.id = :quizId")
    List<Response> findByQuiz_IdWithOptions(@Param("quizId") Long quizId);

    void deleteAllByParticipantIdIn(List<Long> participantIds);

}
