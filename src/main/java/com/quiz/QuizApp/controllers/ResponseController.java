package com.quiz.QuizApp.controllers;

import com.quiz.QuizApp.domain.*;
import com.quiz.QuizApp.dto.ResponseDTO;
import com.quiz.QuizApp.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/responses")
public class ResponseController {

    private final ResponseRepository responseRepo;
    private final ParticipantRepository participantRepo;
    private final QuestionRepository questionRepo;

    public ResponseController(ResponseRepository responseRepo, ParticipantRepository participantRepo, QuestionRepository questionRepo) {
        this.responseRepo = responseRepo;
        this.participantRepo = participantRepo;
        this.questionRepo = questionRepo;
    }

    @PostMapping
    @Transactional
    public ResponseEntity<String> submitResponse(@RequestBody ResponseDTO dto) {
        Participant participant = participantRepo.findAll().stream()
                .filter(p -> p.getPhoneNumber().equals(dto.getPhoneNumber()))
                .findFirst()
                .orElse(null);

        if (participant == null) return ResponseEntity.badRequest().body("Invalid participant phone number");

        Question question = questionRepo.findById(dto.getQuestionId()).orElse(null);
        if (question == null) return ResponseEntity.badRequest().body("Invalid question ID");

        Response response = new Response();
        response.setUsername(dto.getUsername());
        response.setParticipant(participant);
        response.setQuestion(question);
        response.setSelectedAnswer(dto.getSelectedAnswer());

        responseRepo.save(response);
        return ResponseEntity.ok("Response submitted successfully");
    }

    @GetMapping("/results/{quizId}")
    public ResponseEntity<?> getResults(@PathVariable Long quizId) {
        var responses = responseRepo.findByParticipant_Quiz_Id(quizId);

        Map<String, Integer> scores = new HashMap<>();

        for (Response response : responses) {
            String username = response.getUsername();
            boolean isCorrect = response.getQuestion().getOptions().stream()
                    .filter(opt -> opt.getText().equals(response.getSelectedAnswer()))
                    .findFirst()
                    .map(AnswerOption::isCorrect)
                    .orElse(false);

            scores.put(username, scores.getOrDefault(username, 0) + (isCorrect ? 1 : 0));
        }

        List<Map<String, Object>> resultList = scores.entrySet().stream()
                .map(entry -> {
                    Map<String, Object> result = new HashMap<>();
                    result.put("username", entry.getKey());
                    result.put("score", entry.getValue());
                    return result;
                }).toList();

        return ResponseEntity.ok(resultList);
    }

}
