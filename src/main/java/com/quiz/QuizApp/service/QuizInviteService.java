package com.quiz.QuizApp.service;

import com.quiz.QuizApp.domain.Participant;
import com.quiz.QuizApp.domain.Quiz;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuizInviteService {

    private final TwilioService twilioService;
    private final QuizService quizService;

    public QuizInviteService(TwilioService twilioService, QuizService quizService) {
        this.twilioService = twilioService;
        this.quizService = quizService;
    }

    public void sendQuizInvites(Long quizId) {
        Quiz quiz = quizService.getQuizById(quizId);
        if (quiz == null) {
            throw new RuntimeException("Quiz not found");
        }

        // Collect phone numbers of all participants
        List<String> phoneNumbers = quiz.getParticipants().stream()
                .map(Participant::getPhoneNumber)
                .collect(Collectors.toList());

        // Send invites to all participants
        twilioService.sendQuizInvites(phoneNumbers, quizId);
    }
}
