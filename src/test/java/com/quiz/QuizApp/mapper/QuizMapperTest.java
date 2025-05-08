package com.quiz.QuizApp.mapper;

import com.quiz.QuizApp.domain.*;
import com.quiz.QuizApp.dto.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class QuizMapperTest {

    @Test
    void shouldMapDtoToEntity() {
        QuizDTO dto = new QuizDTO();
        dto.setTitle("Test Quiz");
        dto.setDurationInSeconds(60);

        QuestionDTO qDto = new QuestionDTO();
        qDto.setText("What is Java?");

        AnswerOptionDTO a1 = new AnswerOptionDTO();
        a1.setText("A programming language");
        a1.setCorrect(true);

        AnswerOptionDTO a2 = new AnswerOptionDTO();
        a2.setText("A coffee brand");
        a2.setCorrect(false);

        qDto.setOptions(List.of(a1, a2));
        dto.setQuestions(List.of(qDto));

        ParticipantDTO pDto = new ParticipantDTO();
        pDto.setPhoneNumber("+1234567890");
        dto.setParticipants(List.of(pDto));

        Quiz quiz = QuizMapper.fromDto(dto);

        assertEquals("Test Quiz", quiz.getTitle());
        assertEquals(60, quiz.getDurationInSeconds());
        assertEquals(1, quiz.getQuestions().size());
        assertEquals("What is Java?", quiz.getQuestions().get(0).getText());
        assertEquals(2, quiz.getQuestions().get(0).getOptions().size());
        assertEquals(1, quiz.getParticipants().size());
    }

    @Test
    void shouldMapEntityToDto() {
        Quiz quiz = new Quiz();
        quiz.setId(1L);
        quiz.setTitle("Mapped Quiz");

        Question question = new Question();
        question.setText("What is Spring?");

        AnswerOption opt1 = new AnswerOption();
        opt1.setText("Framework");
        opt1.setCorrect(true);

        question.setOptions(List.of(opt1));
        quiz.setQuestions(List.of(question));

        Participant p = new Participant();
        p.setPhoneNumber("+1111111111");
        quiz.setParticipants(List.of(p));

        QuizDTO dto = QuizMapper.toDto(quiz);

        assertEquals("Mapped Quiz", dto.getTitle());
        assertEquals(1, dto.getQuestions().size());
        assertEquals("What is Spring?", dto.getQuestions().get(0).getText());
        assertEquals(1, dto.getQuestions().get(0).getOptions().size());
        assertEquals("Framework", dto.getQuestions().get(0).getOptions().get(0).getText());
        assertEquals(1, dto.getParticipants().size());
        assertEquals("+1111111111", dto.getParticipants().get(0).getPhoneNumber());
    }
}
