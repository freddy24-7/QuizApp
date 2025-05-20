package com.quiz.QuizApp.service;

import com.quiz.QuizApp.domain.Participant;
import com.quiz.QuizApp.domain.Quiz;
import com.quiz.QuizApp.dto.QuizDTO;
import com.quiz.QuizApp.mapper.QuizMapper;
import com.quiz.QuizApp.repository.ParticipantRepository;
import com.quiz.QuizApp.repository.QuizRepository;
import com.quiz.QuizApp.repository.ResponseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class QuizService {

    private final QuizRepository quizRepo;
    private final ParticipantRepository participantRepo;
    private final ResponseRepository responseRepo;

    public QuizService(
            QuizRepository quizRepo,
            ParticipantRepository participantRepo,
            ResponseRepository responseRepo
    ) {
        this.quizRepo = quizRepo;
        this.participantRepo = participantRepo;
        this.responseRepo = responseRepo;
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
    public Quiz getQuizById(Long id) {
        return quizRepo.findByIdWithParticipants(id).orElse(null);
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

        var participantIds = participantRepo.findByQuiz_Id(id)
                .stream()
                .map(Participant::getId)
                .toList();

        responseRepo.deleteAllByParticipantIdIn(participantIds);
        participantRepo.deleteAllByQuiz_Id(id);
        quizRepo.deleteById(id);

        return true;
    }

    @Transactional
    public void deleteAllQuizzes() {
        responseRepo.deleteAll();
        participantRepo.deleteAll();
        quizRepo.deleteAll();
    }

    @Transactional(readOnly = true)
    public Page<Quiz> getQuizPage(Pageable pageable) {
        return quizRepo.findAll(pageable);
    }
}
