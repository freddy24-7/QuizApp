package com.quiz.QuizApp.websocket;

import com.quiz.QuizApp.domain.*;
import com.quiz.QuizApp.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentCaptor.forClass;

@ExtendWith(MockitoExtension.class)
class QuizWebSocketControllerTest {

    @Mock
    private ResponseRepository responseRepo;

    @Mock
    private ParticipantRepository participantRepo;

    @Mock
    private QuestionRepository questionRepo;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private QuizWebSocketController controller;

    @Test
    void shouldBroadcastScoreboardWhenAnswerReceived() {
        AnswerSubmission submission = new AnswerSubmission();
        submission.setPlayerId("+1234567890");
        submission.setUsername("user1");
        submission.setQuestionId(1L);
        submission.setQuizId(99L);
        submission.setSelectedAnswer("Correct Answer");

        Participant participant = new Participant();
        participant.setPhoneNumber("+1234567890");

        Question question = new Question();
        question.setId(1L);

        AnswerOption correctOption = new AnswerOption();
        correctOption.setText("Correct Answer");
        correctOption.setCorrect(true);
        correctOption.setQuestion(question);
        question.setOptions(List.of(correctOption));

        when(participantRepo.findByPhoneNumber("+1234567890")).thenReturn(Optional.of(participant));
        when(questionRepo.findWithOptionsById(1L)).thenReturn(Optional.of(question));
        Response savedResponse = new Response();
        savedResponse.setUsername("user1");
        savedResponse.setParticipant(participant);
        savedResponse.setQuestion(question);
        savedResponse.setSelectedAnswer("Correct Answer");

        when(responseRepo.findByParticipant_Quiz_Id(99L)).thenReturn(List.of(savedResponse));

        var topicCaptor = forClass(String.class);
        var payloadCaptor = forClass(Object.class);

        controller.handleAnswer(submission);

        verify(messagingTemplate).convertAndSend(topicCaptor.capture(), payloadCaptor.capture());

        String topic = topicCaptor.getValue();
        Object payload = payloadCaptor.getValue();

        assertEquals("/topic/scoreboard/99", topic);
        assertNotNull(payload);
        assertInstanceOf(Map.class, payload);

        Map<?, ?> message = (Map<?, ?>) payload;
        assertTrue(message.containsKey("scores"));

        Object scoresObj = message.get("scores");
        assertInstanceOf(List.class, scoresObj);

        List<?> scores = (List<?>) scoresObj;
        assertEquals(1, scores.size());

        Object scoreEntry = scores.get(0);
        assertInstanceOf(Map.class, scoreEntry);

        Map<?, ?> entry = (Map<?, ?>) scoreEntry;
        assertEquals("user1", entry.get("username"));
        assertEquals(1, entry.get("score"));
    }
}
