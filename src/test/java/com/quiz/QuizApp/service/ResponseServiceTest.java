package com.quiz.QuizApp.service;

import com.quiz.QuizApp.domain.*;
import com.quiz.QuizApp.dto.ResponseDTO;
import com.quiz.QuizApp.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResponseServiceTest {

    @Mock
    private ResponseRepository responseRepo;

    @Mock
    private ParticipantRepository participantRepo;

    @Mock
    private QuestionRepository questionRepo;

    @Mock
    private QuizRepository quizRepo;

    @InjectMocks
    private ResponseService responseService;

    @Test
    void shouldRejectInvalidPhoneNumber() {
        // given
        ResponseDTO dto = new ResponseDTO();
        dto.setPhoneNumber("+0000000000");
        when(participantRepo.findAll()).thenReturn(List.of());

        // when
        ResponseEntity<String> result = responseService.submitResponse(dto);

        // then
        assertEquals(400, result.getStatusCodeValue());
        assertTrue(result.getBody().contains("Invalid participant phone number"));
    }

    @Test
    void shouldRejectInvalidQuestionId() {
        // given
        ResponseDTO dto = new ResponseDTO();
        dto.setPhoneNumber("+1234567890");
        dto.setQuestionId(99L);
        Participant participant = new Participant();
        participant.setPhoneNumber("+1234567890");
        when(participantRepo.findAll()).thenReturn(List.of(participant));
        when(questionRepo.findById(99L)).thenReturn(Optional.empty());

        // when
        ResponseEntity<String> result = responseService.submitResponse(dto);

        // then
        assertEquals(400, result.getStatusCodeValue());
        assertTrue(result.getBody().contains("Invalid question ID"));
    }

    @Test
    void shouldAcceptValidSubmission() {
        // given
        ResponseDTO dto = new ResponseDTO();
        dto.setPhoneNumber("+1234567890");
        dto.setQuestionId(1L);
        dto.setSelectedAnswer("4");
        dto.setUsername("testUser");

        Participant participant = new Participant();
        participant.setId(1L);
        participant.setPhoneNumber("+1234567890");

        Question question = new Question();
        question.setId(1L);
        Quiz quiz = new Quiz();
        question.setQuiz(quiz);

        when(participantRepo.findAll()).thenReturn(List.of(participant));
        when(questionRepo.findById(1L)).thenReturn(Optional.of(question));
        when(responseRepo.findByParticipant_IdAndQuestion_Id(1L, 1L)).thenReturn(Optional.empty());

        // when
        ResponseEntity<String> result = responseService.submitResponse(dto);

        // then
        assertEquals(200, result.getStatusCodeValue());
        assertTrue(result.getBody().contains("Response submitted"));
    }
}
