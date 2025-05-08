package com.quiz.QuizApp.controllers;

import com.quiz.QuizApp.dto.ResponseDTO;
import com.quiz.QuizApp.service.ResponseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/responses")
@RequiredArgsConstructor
public class ResponseController {

    private final ResponseService responseService;

    @PostMapping
    public ResponseEntity<String> submitResponse(@RequestBody ResponseDTO dto) {
        return responseService.submitResponse(dto);
    }

    @DeleteMapping("/reset/{quizId}")
    public ResponseEntity<?> resetQuizResponses(@PathVariable Long quizId) {
        return responseService.resetResponses(quizId);
    }

    @GetMapping("/results/{quizId}")
    public ResponseEntity<?> getResults(
            @PathVariable Long quizId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return responseService.getResults(quizId, page, size);
    }

    @GetMapping("/progress/{quizId}")
    public ResponseEntity<?> getProgress(
            @PathVariable Long quizId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return responseService.getProgress(quizId, page, size);
    }
}
