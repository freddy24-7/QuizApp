package com.quiz.QuizApp.controllers;

import com.quiz.QuizApp.dto.QuizDTO;
import com.quiz.QuizApp.dto.QuizSummaryDTO;
import com.quiz.QuizApp.domain.Quiz;
import com.quiz.QuizApp.mapper.QuizMapper;
import com.quiz.QuizApp.service.QuizInviteService;
import com.quiz.QuizApp.service.QuizService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(QuizController.class);

    private final QuizService quizService;
    private final QuizInviteService quizInviteService;

    public QuizController(QuizService quizService, QuizInviteService quizInviteService) {
        this.quizService = quizService;
        this.quizInviteService = quizInviteService;
    }

    @PostMapping
    public ResponseEntity<QuizDTO> createQuiz(@Valid @RequestBody QuizDTO dto) {

        if (dto.getTitle() == null || dto.getTitle().isEmpty()) {
            logger.warn("Quiz title is missing or empty");
            return ResponseEntity.badRequest().body(new QuizDTO());
        }

        if (dto.getQuestions() == null || dto.getQuestions().isEmpty()) {
            logger.warn("No questions provided for the quiz");
            return ResponseEntity.badRequest().body(new QuizDTO());
        }

        try {
            logger.info("Received request to create quiz with title: {}", dto.getTitle());

            // Create the quiz
            var created = quizService.createQuiz(dto);
            logger.info("Quiz created successfully with ID: {}", created.getId());

            // Send the quiz invite
            quizInviteService.sendQuizInvites(created.getId());
            logger.info("Invites sent successfully for quiz with ID: {}", created.getId());

            return ResponseEntity.ok(QuizMapper.toDto(created));

        } catch (Exception e) {
            logger.error("Error occurred while creating quiz or sending invites", e);
            return ResponseEntity.status(500).body(new QuizDTO());
        }
    }

    @GetMapping
    public List<QuizDTO> getAll() {
        try {
            logger.info("Fetching all quizzes");
            List<QuizDTO> quizzes = quizService.getAllQuizzes();
            logger.info("Fetched {} quizzes", quizzes.size());
            return quizzes;
        } catch (Exception e) {
            logger.error("Error occurred while fetching quizzes", e);
            return List.of();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuizDTO> getOne(@PathVariable Long id) {
        try {
            logger.info("Fetching quiz with ID: {}", id);
            Quiz quiz = quizService.getQuizById(id);

            if (quiz == null) {
                logger.warn("Quiz with ID: {} not found", id);
                return ResponseEntity.notFound().build();
            }

            logger.info("Fetched quiz with ID: {}", id);
            QuizDTO dto = QuizMapper.toDto(quiz);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            logger.error("Error occurred while fetching quiz with ID: {}", id, e);
            return ResponseEntity.status(500).body(new QuizDTO());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<QuizDTO> updateQuiz(@PathVariable Long id, @Valid @RequestBody QuizDTO dto) {
        try {
            logger.info("Received request to update quiz with ID: {}", id);

            var updated = quizService.updateQuiz(id, dto);
            if (updated == null) {
                logger.warn("Quiz with ID: {} not found for update", id);
                return ResponseEntity.notFound().build();
            }

            logger.info("Quiz with ID: {} updated successfully", id);
            return ResponseEntity.ok(QuizMapper.toDto(updated));

        } catch (Exception e) {
            logger.error("Error occurred while updating quiz with ID: {}", id, e);
            return ResponseEntity.status(500).body(new QuizDTO());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuiz(@PathVariable Long id) {
        try {
            logger.info("Received request to delete quiz with ID: {}", id);
            boolean deleted = quizService.deleteQuiz(id);

            if (deleted) {
                logger.info("Quiz with ID: {} deleted successfully", id);
                return ResponseEntity.noContent().build();
            } else {
                logger.warn("Quiz with ID: {} not found for deletion", id);
                return ResponseEntity.notFound().build();
            }

        } catch (Exception e) {
            logger.error("Error occurred while deleting quiz with ID: {}", id, e);
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/summaries")
    public Page<QuizSummaryDTO> getSummaries(@PageableDefault(size = 5) Pageable pageable) {
        try {
            logger.info("Fetching quiz summaries with page size: {}", pageable.getPageSize());
            Page<QuizSummaryDTO> summaries = quizService.getQuizPage(pageable)
                    .map(QuizMapper::toSummaryDto);
            logger.info("Fetched {} quiz summaries", summaries.getTotalElements());
            return summaries;
        } catch (Exception e) {
            logger.error("Error occurred while fetching quiz summaries", e);
            return Page.empty();
        }
    }

    @DeleteMapping
    public ResponseEntity<String> deleteAllQuizzes() {
        try {
            logger.info("Received request to delete all quizzes");
            quizService.deleteAllQuizzes();
            logger.info("All quizzes deleted successfully");
            return ResponseEntity.ok("All quizzes have been deleted.");
        } catch (Exception e) {
            logger.error("Error occurred while deleting all quizzes", e);
            return ResponseEntity.status(500).body("Failed to delete quizzes.");
        }
    }
}
