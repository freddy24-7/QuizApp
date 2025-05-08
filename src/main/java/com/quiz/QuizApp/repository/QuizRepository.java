package com.quiz.QuizApp.repository;

import com.quiz.QuizApp.domain.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizRepository extends JpaRepository<Quiz, Long> {

}
