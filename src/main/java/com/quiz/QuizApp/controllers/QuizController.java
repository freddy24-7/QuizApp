package com.quiz.QuizApp.controllers;

import com.quiz.QuizApp.dto.QuizDTO;
import com.quiz.QuizApp.dto.QuizSummaryDTO;
import com.quiz.QuizApp.domain.Quiz;
import com.quiz.QuizApp.mapper.QuizMapper;
import com.quiz.QuizApp.service.QuizInviteService;
import com.quiz.QuizApp.service.QuizService;
import jakarta.validation.Valid;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@RestController
@RequestMapping("/api/quizzes")
public class QuizController {

    private final QuizService quizService;
    private final QuizInviteService quizInviteService;


    public QuizController(QuizService quizService, QuizInviteService quizInviteService) {
        this.quizService = quizService;
        this.quizInviteService = quizInviteService;
    }

    @PostMapping
    public ResponseEntity<QuizDTO> createQuiz(@Valid @RequestBody QuizDTO dto) {
        var created = quizService.createQuiz(dto);

        quizInviteService.sendQuizInvites(created.getId());

        return ResponseEntity.ok(QuizMapper.toDto(created));
    }

    @GetMapping
    public List<QuizDTO> getAll() {
        return quizService.getAllQuizzes();
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuizDTO> getOne(@PathVariable Long id) {
        Quiz quiz = quizService.getQuizById(id);

        if (quiz == null) {
            return ResponseEntity.notFound().build();
        }

        QuizDTO dto = QuizMapper.toDto(quiz);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<QuizDTO> updateQuiz(@PathVariable Long id, @Valid @RequestBody QuizDTO dto) {
        var updated = quizService.updateQuiz(id, dto);
        return updated != null ? ResponseEntity.ok(QuizMapper.toDto(updated)) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuiz(@PathVariable Long id) {
        boolean deleted = quizService.deleteQuiz(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @GetMapping("/summaries")
    public Page<QuizSummaryDTO> getSummaries(@PageableDefault(size = 5) Pageable pageable) {
        return quizService.getQuizPage(pageable)
                .map(QuizMapper::toSummaryDto);
    }

    @DeleteMapping
    public ResponseEntity<String> deleteAllQuizzes() {
        quizService.deleteAllQuizzes();
        return ResponseEntity.ok("All quizzes have been deleted.");
    }
}
