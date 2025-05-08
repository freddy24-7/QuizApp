package com.quiz.QuizApp.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quiz.QuizApp.domain.*;
import com.quiz.QuizApp.dto.ResponseDTO;
import com.quiz.QuizApp.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ResponseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private QuizRepository quizRepo;
    @Autowired
    private QuestionRepository questionRepo;
    @Autowired
    private ParticipantRepository participantRepo;

    private Long questionId;
    private String phoneNumber;

    @BeforeEach
    void setup() {
        Quiz quiz = new Quiz();
        quiz.setTitle("Response Test Quiz");
        quiz.setDurationInSeconds(60);

        Question question = new Question();
        question.setText("What is HTTP?");
        question.setQuiz(quiz);

        AnswerOption option = new AnswerOption();
        option.setText("Hypertext Transfer Protocol");
        option.setCorrect(true);
        option.setQuestion(question);

        question.setOptions(List.of(option));
        quiz.setQuestions(List.of(question));

        Participant participant = new Participant();
        participant.setPhoneNumber("+1234567899");
        participant.setQuiz(quiz);

        quiz.setParticipants(List.of(participant));

        quizRepo.save(quiz);

        this.questionId = question.getId();
        this.phoneNumber = participant.getPhoneNumber();
    }

    @Test
    void shouldSubmitResponseSuccessfully() throws Exception {
        ResponseDTO dto = new ResponseDTO();
        dto.setPhoneNumber(phoneNumber);
        dto.setQuestionId(questionId);
        dto.setSelectedAnswer("Hypertext Transfer Protocol");
        dto.setUsername("testUser");

        mockMvc.perform(post("/api/responses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }
}
