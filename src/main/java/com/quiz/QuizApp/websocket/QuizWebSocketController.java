package com.quiz.QuizApp.websocket;

import com.quiz.QuizApp.domain.AnswerOption;
import com.quiz.QuizApp.domain.Participant;
import com.quiz.QuizApp.domain.Question;
import com.quiz.QuizApp.domain.Response;
import com.quiz.QuizApp.repository.ParticipantRepository;
import com.quiz.QuizApp.repository.QuestionRepository;
import com.quiz.QuizApp.repository.ResponseRepository;
import jakarta.transaction.Transactional;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class QuizWebSocketController {

    private final ResponseRepository    responseRepo;
    private final ParticipantRepository participantRepo;
    private final QuestionRepository    questionRepo;
    private final SimpMessagingTemplate messagingTemplate;

    public QuizWebSocketController(ResponseRepository responseRepo,
                                   ParticipantRepository participantRepo,
                                   QuestionRepository questionRepo,
                                   SimpMessagingTemplate messagingTemplate) {
        this.responseRepo      = responseRepo;
        this.participantRepo   = participantRepo;
        this.questionRepo      = questionRepo;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/submit-answer")
    @Transactional
    public void handleAnswer(AnswerSubmission submission) {
        // 1. Find participant
        Participant participant = participantRepo
                .findByPhoneNumber(submission.getPlayerId())
                .orElse(null);
        if (participant == null) {
            return;
        }

        // 2. Find question with options eagerly fetched
        Question question = questionRepo
                .findWithOptionsById(submission.getQuestionId())
                .orElse(null);
        if (question == null) {
            return;
        }

        // 3. Save or update response
        Response response = responseRepo
                .findByParticipant_IdAndQuestion_Id(participant.getId(), question.getId())
                .orElse(new Response());
        response.setUsername(submission.getUsername());
        response.setParticipant(participant);
        response.setQuestion(question);
        response.setSelectedAnswer(submission.getSelectedAnswer());
        responseRepo.save(response);

        // 4. Recalculate scores
        List<Response> allResponses =
                responseRepo.findByParticipant_Quiz_Id(submission.getQuizId());
        Map<String, Integer> scores = new HashMap<>();
        for (Response r : allResponses) {
            boolean correct = r.getQuestion().getOptions().stream()
                    .filter(opt -> opt.getText().equals(r.getSelectedAnswer()))
                    .findFirst()
                    .map(AnswerOption::isCorrect)
                    .orElse(false);
            scores.merge(r.getUsername(), correct ? 1 : 0, Integer::sum);
        }

        // 5. Build scoreboard list
        List<Map<String, Object>> scoreboard = scores.entrySet().stream()
                .map(e -> {
                    Map<String, Object> row = new HashMap<>();
                    row.put("username", e.getKey());
                    row.put("score", e.getValue());
                    return row;
                })
                .sorted((a, b) ->
                        ((Integer) b.get("score")).compareTo((Integer) a.get("score")))
                .collect(Collectors.toList());

        // 6. Prepare and send message
        Map<String, Object> message = new HashMap<>();
        message.put("type",      "scoreboard");
        message.put("quizId",    submission.getQuizId());
        message.put("timestamp", new Date().toString());
        message.put("scores",    scoreboard);

        messagingTemplate.convertAndSend(
                "/topic/scoreboard/" + submission.getQuizId(),
                message
        );
    }
}
