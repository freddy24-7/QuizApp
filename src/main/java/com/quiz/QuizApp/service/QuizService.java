package com.quiz.QuizApp.service;

import com.quiz.QuizApp.domain.Quiz;
import com.quiz.QuizApp.dto.QuizDTO;
import com.quiz.QuizApp.mapper.QuizMapper;
import com.quiz.QuizApp.repository.ParticipantRepository;
import com.quiz.QuizApp.repository.QuizRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class QuizService {

    private final QuizRepository quizRepo;
    private final ParticipantRepository participantRepo;

    public QuizService(QuizRepository quizRepo, ParticipantRepository participantRepo) {
        this.quizRepo = quizRepo;
        this.participantRepo = participantRepo;
    }

    public Quiz createQuiz(QuizDTO dto) {
        return quizRepo.save(QuizMapper.fromDto(dto));
    }

    @Transactional(readOnly = true)
    public List<QuizDTO> getAllQuizzes() {
        return quizRepo.findAll().stream()
                .map(QuizMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public QuizDTO getQuizById(Long id) {
        return quizRepo.findById(id)
                .map(QuizMapper::toDto)
                .orElse(null);
    }

    public Quiz updateQuiz(Long id, QuizDTO dto) {
        if (!quizRepo.existsById(id)) return null;
        Quiz quiz = QuizMapper.fromDto(dto);
        quiz.setId(id);
        return quizRepo.save(quiz);
    }

    @Transactional
    public boolean deleteQuiz(Long id) {
        if (!quizRepo.existsById(id)) return false;
        participantRepo.deleteAllByQuiz_Id(id);  // Delete participants first
        quizRepo.deleteById(id);                // Then delete quiz
        return true;
    }

    @Transactional
    public void deleteAllQuizzes() {
        participantRepo.deleteAll();  // Delete all participants first
        quizRepo.deleteAll();         // Then delete all quizzes
    }

    @Transactional(readOnly = true)
    public Page<Quiz> getQuizPage(Pageable pageable) {
        return quizRepo.findAll(pageable);
    }
}
