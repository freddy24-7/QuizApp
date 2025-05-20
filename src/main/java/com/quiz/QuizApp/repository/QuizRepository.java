package com.quiz.QuizApp.repository;

import com.quiz.QuizApp.domain.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface QuizRepository extends JpaRepository<Quiz, Long> {

    @Query("SELECT q FROM Quiz q LEFT JOIN FETCH q.participants WHERE q.id = :id")
    Optional<Quiz> findByIdWithParticipants(@Param("id") Long id);

}
