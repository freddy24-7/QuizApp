package com.quiz.QuizApp.websocket;

import com.quiz.QuizApp.domain.*;
import com.quiz.QuizApp.repository.QuizRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.test.context.ActiveProfiles;
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

    @LocalServerPort
    private int port;

    @Autowired
    private QuizRepository quizRepo;

    private static final String SUBSCRIBE_DEST = "/topic/scoreboard/";

    @Test
    void shouldReceiveScoreboardMessageViaWebSocket() throws Exception {
        // 1) Build & persist quiz graph
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

        // commit to DB so the controller can see it
        quizRepo.saveAndFlush(quiz);

        Long quizId      = quiz.getId();
        Long questionId  = question.getId();
        String phone     = participant.getPhoneNumber();

        // 2) Prepare to collect messages
        BlockingQueue<Object> messages = new LinkedBlockingDeque<>();

        // 3) Connect to the STOMP endpoint
        String wsUrl = "ws://localhost:" + port + "/ws";
        WebSocketStompClient stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        StompSession session = stompClient
                .connectAsync(wsUrl, new StompSessionHandlerAdapter() {})
                .get(5, TimeUnit.SECONDS);

        // 4) Subscribe to the real-time scoreboard
        session.subscribe(SUBSCRIBE_DEST + quizId, new StompFrameHandler() {
            @Override public Type getPayloadType(StompHeaders headers) { return Object.class; }
            @Override public void handleFrame(StompHeaders headers, Object payload) {
                messages.offer(payload);
            }
        });

        // 5) Send an answer
        AnswerSubmission submission = new AnswerSubmission();
        submission.setQuizId(quizId);
        submission.setQuestionId(questionId);
        submission.setSelectedAnswer("4");
        submission.setPlayerId(phone);
        submission.setUsername("wsUser");

        session.send("/app/submit-answer", submission);

        // 6) Wait up to 10s for a message
        Object msg = messages.poll(10, TimeUnit.SECONDS);
        assertNotNull(msg, "Expected a real-time scoreboard update");
    }
}
