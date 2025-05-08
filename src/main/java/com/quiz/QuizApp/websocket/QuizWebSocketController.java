package com.quiz.QuizApp.websocket;

import com.quiz.QuizApp.domain.*;
import com.quiz.QuizApp.repository.*;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class QuizWebSocketController {

    private final ResponseRepository responseRepo;
    private final ParticipantRepository participantRepo;
    private final QuestionRepository questionRepo;
    private final SimpMessagingTemplate messagingTemplate;

    public QuizWebSocketController(ResponseRepository responseRepo,
                                   ParticipantRepository participantRepo,
                                   QuestionRepository questionRepo,
                                   SimpMessagingTemplate messagingTemplate) {
        this.responseRepo = responseRepo;
        this.participantRepo = participantRepo;
        this.questionRepo = questionRepo;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/submit-answer")
    public void handleAnswer(AnswerSubmission submission) {
        // 1. Find participant
        Participant participant = participantRepo.findByPhoneNumber(submission.getPlayerId())
                .orElse(null);
        if (participant == null) {
            return;
        }

        // 2. Find question
        Question question = questionRepo.findById(submission.getQuestionId()).orElse(null);
        if (question == null) {
            return;
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

        List<Map<String, Object>> scoreboard = scores.entrySet().stream()
                .map(entry -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("username", entry.getKey());
                    map.put("score", entry.getValue());
                    return map;
                })
                .sorted((a, b) -> ((Integer) b.get("score")).compareTo((Integer) a.get("score")))
                .collect(Collectors.toList());

        Map<String, Object> message = new HashMap<>();
        message.put("type", "scoreboard");
        message.put("quizId", submission.getQuizId());
        message.put("timestamp", new Date().toString());
        message.put("scores", scoreboard);

        messagingTemplate.convertAndSend("/topic/scoreboard/" + submission.getQuizId(), message);
    }
}
