package com.quiz.QuizApp.service;

import com.quiz.QuizApp.domain.Quiz;
import com.quiz.QuizApp.dto.QuizDTO;
import com.quiz.QuizApp.dto.QuestionDTO;
import com.quiz.QuizApp.dto.AnswerOptionDTO;
import com.quiz.QuizApp.dto.ParticipantDTO;
import com.quiz.QuizApp.repository.QuizRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
class QuizServiceTest {

    @Mock
    private QuizRepository quizRepo;

    @InjectMocks
    private QuizService quizService;

    @Test
    void shouldCreateQuizSuccessfully() {
        QuizDTO dto = new QuizDTO();
        dto.setTitle("Unit Test Quiz");
        dto.setDurationInSeconds(120);

        QuestionDTO question = new QuestionDTO();
        question.setText("What is Java?");

        AnswerOptionDTO option1 = new AnswerOptionDTO();
        option1.setText("A programming language");
        option1.setCorrect(true);

        AnswerOptionDTO option2 = new AnswerOptionDTO();
        option2.setText("Coffee");
        option2.setCorrect(false);

        question.setOptions(List.of(option1, option2));
        dto.setQuestions(List.of(question));

        ParticipantDTO participant = new ParticipantDTO();
        participant.setPhoneNumber("+15551234567");
        dto.setParticipants(List.of(participant));

        when(quizRepo.save(any(Quiz.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Quiz quiz = quizService.createQuiz(dto);

        assertEquals("Unit Test Quiz", quiz.getTitle());
        assertEquals(1, quiz.getQuestions().size());
        assertEquals(1, quiz.getParticipants().size());
    }
}
