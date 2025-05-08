package com.quiz.QuizApp.service;

import com.quiz.QuizApp.domain.*;
import com.quiz.QuizApp.dto.ResponseDTO;
import com.quiz.QuizApp.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ResponseService {

    private final ResponseRepository responseRepo;
    private final ParticipantRepository participantRepo;
    private final QuestionRepository questionRepo;
    private final QuizRepository quizRepo;

    @Transactional
    public ResponseEntity<String> submitResponse(ResponseDTO dto) {
        Participant participant = participantRepo.findAll().stream()
                .filter(p -> p.getPhoneNumber().equals(dto.getPhoneNumber()))
                .findFirst()
                .orElse(null);

        if (participant == null) return ResponseEntity.badRequest().body("Invalid participant phone number");

        Question question = questionRepo.findById(dto.getQuestionId()).orElse(null);
        if (question == null) return ResponseEntity.badRequest().body("Invalid question ID");

        Quiz quiz = question.getQuiz();
        if (quiz.isClosed()) {
            return ResponseEntity.badRequest().body("This quiz is closed.");
        }

        if (quiz.getStartTime() != null && quiz.getDurationInSeconds() != null) {
            LocalDateTime endTime = quiz.getStartTime().plusSeconds(quiz.getDurationInSeconds());
            if (LocalDateTime.now().isAfter(endTime)) {
                quiz.setClosed(true);
                quizRepo.save(quiz);
                return ResponseEntity.badRequest().body("Time is up. The quiz has been closed.");
            }
        }

        Response response = responseRepo
                .findByParticipant_IdAndQuestion_Id(participant.getId(), question.getId())
                .orElse(new Response());

        response.setUsername(dto.getUsername());
        response.setParticipant(participant);
        response.setQuestion(question);
        response.setSelectedAnswer(dto.getSelectedAnswer());

        responseRepo.save(response);

        return ResponseEntity.ok("Response submitted (or updated) successfully");
    }

    @Transactional
    public ResponseEntity<?> resetResponses(Long quizId) {
        List<Response> toDelete = responseRepo.findByParticipant_Quiz_Id(quizId);
        responseRepo.deleteAll(toDelete);
        return ResponseEntity.ok("All responses for quiz " + quizId + " have been reset.");
    }

    public ResponseEntity<?> getResults(Long quizId, int page, int size) {
        var responses = responseRepo.findByParticipant_Quiz_Id(quizId);

        Map<String, Integer> scores = new HashMap<>();
        Map<String, String> latestTimestamps = new HashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        for (Response response : responses) {
            String username = response.getUsername();
            boolean isCorrect = response.getQuestion().getOptions().stream()
                    .filter(opt -> opt.getText().equals(response.getSelectedAnswer()))
                    .findFirst()
                    .map(AnswerOption::isCorrect)
                    .orElse(false);

            scores.put(username, scores.getOrDefault(username, 0) + (isCorrect ? 1 : 0));

            String timestamp = response.getCreatedAt() != null ? response.getCreatedAt().format(formatter) : "";
            latestTimestamps.put(username, timestamp);
        }

        List<Map<String, Object>> sortedResults = scores.entrySet().stream()
                .map(entry -> {
                    Map<String, Object> result = new HashMap<>();
                    result.put("username", entry.getKey());
                    result.put("score", entry.getValue());
                    result.put("lastSubmittedAt", latestTimestamps.get(entry.getKey()));
                    return result;
                })
                .sorted((a, b) -> ((Integer) b.get("score")).compareTo((Integer) a.get("score")))
                .toList();

        return paginateResults(sortedResults, page, size, "results");
    }

    public ResponseEntity<?> getProgress(Long quizId, int page, int size) {
        Optional<Quiz> quizOpt = quizRepo.findById(quizId);
        if (quizOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Quiz quiz = quizOpt.get();
        int totalQuestions = quiz.getQuestions().size();

        List<Response> allResponses = responseRepo.findByParticipant_Quiz_Id(quizId);
        List<Map<String, Object>> progressList = new ArrayList<>();

        for (Participant participant : quiz.getParticipants()) {
            List<Response> participantResponses = allResponses.stream()
                    .filter(r -> r.getParticipant().getId().equals(participant.getId()))
                    .toList();

            long answeredCount = participantResponses.stream()
                    .map(r -> r.getQuestion().getId())
                    .distinct()
                    .count();

            String username = participantResponses.stream()
                    .map(Response::getUsername)
                    .filter(Objects::nonNull)
                    .findFirst()
                    .orElse("Unknown");

            Optional<String> lastSubmittedAt = participantResponses.stream()
                    .map(Response::getCreatedAt)
                    .filter(Objects::nonNull)
                    .map(time -> time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                    .max(String::compareTo);

            double percentage = totalQuestions == 0 ? 0 : (answeredCount * 100.0 / totalQuestions);

            Map<String, Object> progress = new HashMap<>();
            progress.put("username", username);
            progress.put("phoneNumber", participant.getPhoneNumber());
            progress.put("answered", answeredCount);
            progress.put("total", totalQuestions);
            progress.put("completionPercentage", percentage);
            progress.put("completed", answeredCount == totalQuestions);
            progress.put("lastSubmittedAt", lastSubmittedAt.orElse(""));
            progressList.add(progress);
        }

        return paginateResults(progressList, page, size, "progress");
    }

    private ResponseEntity<?> paginateResults(List<Map<String, Object>> fullList, int page, int size, String key) {
        int total = fullList.size();
        int totalPages = (int) Math.ceil((double) total / size);
        int start = Math.min(page * size, total);
        int end = Math.min(start + size, total);

        List<Map<String, Object>> paged = fullList.subList(start, end);

        Map<String, Object> response = new HashMap<>();
        response.put("page", page);
        response.put("size", size);
        response.put("totalPages", totalPages);
        response.put("totalResults", total);
        response.put(key, paged);

        return ResponseEntity.ok(response);
    }
}
