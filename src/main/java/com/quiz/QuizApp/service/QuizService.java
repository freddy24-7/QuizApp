package com.quiz.QuizApp.service;

import com.quiz.QuizApp.domain.*;
import com.quiz.QuizApp.dto.*;
import com.quiz.QuizApp.repository.QuizRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuizService {

    private final QuizRepository quizRepo;

    public QuizService(QuizRepository quizRepo) {
        this.quizRepo = quizRepo;
    }

    public Quiz createQuiz(QuizDTO dto) {
        Quiz quiz = new Quiz();
        quiz.setTitle(dto.getTitle());

        // Map questions and answer options
        List<Question> questions = dto.getQuestions().stream().map(qDto -> {
            Question q = new Question();
            q.setText(qDto.getText());
            q.setQuiz(quiz);

            List<AnswerOption> options = qDto.getOptions().stream().map(optDto -> {
                AnswerOption opt = new AnswerOption();
                opt.setText(optDto.getText());
                opt.setCorrect(optDto.isCorrect());
                opt.setQuestion(q);
                return opt;
            }).collect(Collectors.toList());

            q.setOptions(options);
            return q;
        }).collect(Collectors.toList());

        quiz.setQuestions(questions);

        if (dto.getParticipants() != null) {
            List<Participant> participants = dto.getParticipants().stream().map(pDto -> {
                Participant p = new Participant();
                p.setPhoneNumber(pDto.getPhoneNumber());
                p.setQuiz(quiz);
                return p;
            }).collect(Collectors.toList());

            quiz.setParticipants(participants);
        }

        return quizRepo.save(quiz);
    }

    public List<Quiz> getAllQuizzes() {
        return quizRepo.findAll();
    }

    public Quiz getQuizById(Long id) {
        return quizRepo.findById(id).orElse(null);
    }

    public Quiz updateQuiz(Long id, QuizDTO dto) {
        if (!quizRepo.existsById(id)) return null;

        Quiz quiz = new Quiz();
        quiz.setId(id);
        quiz.setTitle(dto.getTitle());

        List<Question> questions = dto.getQuestions().stream().map(qDto -> {
            Question q = new Question();
            q.setText(qDto.getText());
            q.setQuiz(quiz);

            List<AnswerOption> options = qDto.getOptions().stream().map(optDto -> {
                AnswerOption opt = new AnswerOption();
                opt.setText(optDto.getText());
                opt.setCorrect(optDto.isCorrect());
                opt.setQuestion(q);
                return opt;
            }).toList();

            q.setOptions(options);
            return q;
        }).toList();

        quiz.setQuestions(questions);

        if (dto.getParticipants() != null) {
            List<Participant> participants = dto.getParticipants().stream().map(pDto -> {
                Participant p = new Participant();
                p.setPhoneNumber(pDto.getPhoneNumber());
                p.setQuiz(quiz);
                return p;
            }).toList();
            quiz.setParticipants(participants);
        }

        return quizRepo.save(quiz);
    }

    public boolean deleteQuiz(Long id) {
        if (!quizRepo.existsById(id)) return false;
        quizRepo.deleteById(id);
        return true;
    }

}
