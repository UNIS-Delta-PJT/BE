package unis.project.delta.domain.quiz.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import unis.project.delta.domain.quiz.dto.request.DailyQuizSubmitRequest;
import unis.project.delta.domain.quiz.dto.request.FinanceQuizSubmitRequest;
import unis.project.delta.domain.quiz.dto.response.DailyQuizResponse;
import unis.project.delta.domain.quiz.dto.response.DailyQuizSubmitResponse;
import unis.project.delta.domain.quiz.dto.response.FinanceQuizResponse;
import unis.project.delta.domain.quiz.dto.response.FinanceQuizSubmitResponse;
import unis.project.delta.domain.quiz.service.QuizService;
import unis.project.delta.global.response.ApiResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/quiz")
public class QuizController {

    private final QuizService quizService;

    /**
     * 오늘의 O/X 퀴즈 조회.
     */
    @GetMapping("/daily")
    public ResponseEntity<ApiResponse<DailyQuizResponse>> getDailyQuiz(
            @AuthenticationPrincipal Long userId) {

        DailyQuizResponse response = quizService.getDailyQuiz(userId);
        return ResponseEntity.ok(ApiResponse.success(response, "오늘의 퀴즈 조회 성공"));
    }

    /**
     * O/X 퀴즈 정답 제출.
     */
    @PostMapping("/daily/submit")
    public ResponseEntity<ApiResponse<DailyQuizSubmitResponse>> submitDailyQuiz(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody DailyQuizSubmitRequest request) {

        DailyQuizSubmitResponse response = quizService.submitDailyQuiz(
                userId, request.getQuizId(), request.getAnswer());
        return ResponseEntity.ok(ApiResponse.success(response, "퀴즈 정답 제출 성공"));
    }

    /**
     * 4지선다 금융 퀴즈 조회.
     */
    @GetMapping("/finance")
    public ResponseEntity<ApiResponse<FinanceQuizResponse>> getFinanceQuiz(
            @AuthenticationPrincipal Long userId) {

        FinanceQuizResponse response = quizService.getFinanceQuiz();
        return ResponseEntity.ok(ApiResponse.success(response, "금융 퀴즈 조회 성공"));
    }

    /**
     * 4지선다 금융 퀴즈 정답 제출.
     */
    @PostMapping("/finance/submit")
    public ResponseEntity<ApiResponse<FinanceQuizSubmitResponse>> submitFinanceQuiz(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody FinanceQuizSubmitRequest request) {

        FinanceQuizSubmitResponse response = quizService.submitFinanceQuiz(
                userId, request.getQuizId(), request.getSelectedOption());
        return ResponseEntity.ok(ApiResponse.success(response, "금융 퀴즈 정답 제출 성공"));
    }
}
