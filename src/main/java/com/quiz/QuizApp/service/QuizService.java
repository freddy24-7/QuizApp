package com.quiz.QuizApp.service;

import com.quiz.QuizApp.domain.*;
import com.quiz.QuizApp.dto.*;
import com.quiz.QuizApp.mapper.QuizMapper;
import com.quiz.QuizApp.repository.QuizRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;
import java.util.List;

@Service
public class QuizService {

    private final QuizRepository quizRepo;

    public QuizService(QuizRepository quizRepo) {
        this.quizRepo = quizRepo;
    }

    public Quiz createQuiz(QuizDTO dto) {
        return quizRepo.save(QuizMapper.fromDto(dto));
    }

    public List<Quiz> getAllQuizzes() {
        return quizRepo.findAll();
    }

    public Quiz getQuizById(Long id) {
        return quizRepo.findById(id).orElse(null);
    }

    public Quiz updateQuiz(Long id, QuizDTO dto) {
        if (!quizRepo.existsById(id)) return null;
        Quiz quiz = QuizMapper.fromDto(dto);
        quiz.setId(id);
        return quizRepo.save(quiz);
    }

    public boolean deleteQuiz(Long id) {
        if (!quizRepo.existsById(id)) return false;
        quizRepo.deleteById(id);
        return true;
    }

    public Page<Quiz> getQuizPage(Pageable pageable) {
        return quizRepo.findAll(pageable);
    }

}
