package com.quiz.QuizApp.websocket;

import com.quiz.QuizApp.domain.*;
import com.quiz.QuizApp.repository.*;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class QuizWebSocketController {

    private final ResponseRepository responseRepo;
    private final ParticipantRepository participantRepo;
    private final QuestionRepository questionRepo;

    public QuizWebSocketController(ResponseRepository responseRepo,
                                   ParticipantRepository participantRepo,
                                   QuestionRepository questionRepo) {
        this.responseRepo = responseRepo;
        this.participantRepo = participantRepo;
        this.questionRepo = questionRepo;
    }

    @MessageMapping("/submit-answer")
    @SendTo("/topic/quiz-progress")
    public List<Map<String, Object>> handleAnswer(AnswerSubmission submission) {
        // 1. Find participant
        Participant participant = participantRepo.findByPhoneNumber(submission.getPlayerId())
                .orElse(null);
        if (participant == null) {
            return List.of(Map.of("error", "Invalid phone number"));
        }

        // 2. Find question
        Question question = questionRepo.findById(submission.getQuestionId()).orElse(null);
        if (question == null) {
            return List.of(Map.of("error", "Invalid question ID"));
        }

        // 3. Save response
        Response response = new Response();
        response.setUsername(submission.getUsername());
        response.setParticipant(participant);
        response.setQuestion(question);
        response.setSelectedAnswer(submission.getSelectedAnswer());
        responseRepo.save(response);

        // 4. Recalculate scores for this quiz
        List<Response> allResponses = responseRepo.findByParticipant_Quiz_Id(submission.getQuizId());

        Map<String, Integer> scores = new HashMap<>();
        for (Response r : allResponses) {
            boolean correct = r.getQuestion().getOptions().stream()
                    .filter(opt -> opt.getText().equals(r.getSelectedAnswer()))
                    .findFirst()
                    .map(AnswerOption::isCorrect)
                    .orElse(false);

            scores.put(r.getUsername(), scores.getOrDefault(r.getUsername(), 0) + (correct ? 1 : 0));
        }

        // 5. Return updated leaderboard
        return scores.entrySet().stream()
                .map(entry -> {
                    Map<String, Object> result = new HashMap<>();
                    result.put("username", entry.getKey());
                    result.put("score", entry.getValue());
                    return result;
                }).collect(Collectors.toList());
    }
}
