package com.quiz.QuizApp.websocket;

import com.quiz.QuizApp.domain.*;
import com.quiz.QuizApp.repository.*;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class QuizWebSocketIntegrationTest {

    @Autowired
    private QuizRepository quizRepo;

    private static final String WEBSOCKET_URI = "ws://localhost:8080/ws";
    private static final String SUBSCRIBE_DEST = "/topic/scoreboard/";

    @Test
    @Transactional
    void shouldReceiveScoreboardMessageViaWebSocket() throws Exception {
        // Set up the quiz
        Quiz quiz = new Quiz();
        quiz.setTitle("WebSocket Test Quiz");
        quiz.setDurationInSeconds(120);

        Question question = new Question();
        question.setText("What is 2+2?");
        question.setQuiz(quiz);

        AnswerOption option = new AnswerOption();
        option.setText("4");
        option.setCorrect(true);
        option.setQuestion(question);

        question.setOptions(List.of(option));
        quiz.setQuestions(List.of(question));

        Participant participant = new Participant();
        participant.setPhoneNumber("+9988776655");
        participant.setQuiz(quiz);

        quiz.setParticipants(List.of(participant));
        quizRepo.saveAndFlush(quiz);

        // Manually initialize collections
        Quiz saved = quizRepo.findById(quiz.getId()).orElseThrow();
        Hibernate.initialize(saved.getQuestions());
        saved.getQuestions().forEach(q -> Hibernate.initialize(q.getOptions()));
        Hibernate.initialize(saved.getParticipants());

        Long quizId = saved.getId();
        Long questionId = saved.getQuestions().get(0).getId();
        String phoneNumber = saved.getParticipants().get(0).getPhoneNumber();

        BlockingQueue<Object> messages = new LinkedBlockingDeque<>();

        WebSocketStompClient stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        StompSession session = stompClient
                .connectAsync(WEBSOCKET_URI, new StompSessionHandlerAdapter() {})
                .get(1, TimeUnit.SECONDS);

        session.subscribe(SUBSCRIBE_DEST + quizId, new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return Object.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                messages.offer(payload);
            }
        });

        AnswerSubmission submission = new AnswerSubmission();
        submission.setQuizId(quizId);
        submission.setQuestionId(questionId);
        submission.setSelectedAnswer("4");
        submission.setPlayerId(phoneNumber);
        submission.setUsername("wsUser");

        session.send("/app/submit-answer", submission);

        Object message = messages.poll(3, TimeUnit.SECONDS);
        assertNotNull(message, "Expected to receive scoreboard update but got none");
    }
}
