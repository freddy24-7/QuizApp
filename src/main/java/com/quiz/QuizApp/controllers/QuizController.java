package com.quiz.QuizApp.controllers;

import com.quiz.QuizApp.domain.*;
import com.quiz.QuizApp.dto.*;
import com.quiz.QuizApp.service.QuizService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quizzes")
public class QuizController {

    private final QuizService quizService;

    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    @PostMapping
    public ResponseEntity<Quiz> createQuiz(@Valid @RequestBody QuizDTO dto) {
        return ResponseEntity.ok(quizService.createQuiz(dto));
    }

    @GetMapping
    public List<Quiz> getAll() {
        return quizService.getAllQuizzes();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Quiz> getOne(@PathVariable Long id) {
        Quiz quiz = quizService.getQuizById(id);
        return quiz != null ? ResponseEntity.ok(quiz) : ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Quiz> updateQuiz(@PathVariable Long id, @Valid @RequestBody QuizDTO dto) {
        Quiz updated = quizService.updateQuiz(id, dto);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuiz(@PathVariable Long id) {
        boolean deleted = quizService.deleteQuiz(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

}
