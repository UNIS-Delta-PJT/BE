package unis.project.delta.domain.quiz.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import unis.project.delta.domain.quiz.dto.response.*;
import unis.project.delta.domain.quiz.entity.*;
import unis.project.delta.domain.quiz.repository.*;
import unis.project.delta.domain.user.entity.User;
import unis.project.delta.domain.user.repository.UserRepository;
import unis.project.delta.global.exception.CustomException;
import unis.project.delta.global.exception.ErrorCode;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class QuizService {

    private static final int QUIZ_REWARD_COIN = 1;

    private final DailyQuizRepository dailyQuizRepository;
    private final FinanceQuizRepository financeQuizRepository;
    private final UserDailyQuizHistoryRepository dailyQuizHistoryRepository;
    private final UserFinanceQuizHistoryRepository financeQuizHistoryRepository;
    private final UserRepository userRepository;

    // ════════════════════════════════════════
    //  O/X 일일 퀴즈
    // ════════════════════════════════════════

    /**
     * 오늘의 O/X 퀴즈를 조회한다.
     * 이미 제출했으면 결과와 해설을 함께 반환한다.
     */
    @Transactional(readOnly = true)
    public DailyQuizResponse getDailyQuiz(Long userId) {
        User user = findByUserId(userId);
        DailyQuiz quiz = selectTodaysDailyQuiz();
        LocalDate today = LocalDate.now();

        return dailyQuizHistoryRepository.findByUserAndTargetDate(user, today)
                .map(history -> DailyQuizResponse.submitted(quiz, history))
                .orElse(DailyQuizResponse.notSubmitted(quiz));
    }

    /**
     * O/X 퀴즈 정답을 제출한다.
     * 정답이면 1코인을 지급한다. 하루에 한 번만 제출 가능하다.
     */
    @Transactional
    public DailyQuizSubmitResponse submitDailyQuiz(Long userId, Long quizId, String answer) {
        User user = findByUserId(userId);
        LocalDate today = LocalDate.now();

        // 중복 제출 검사
        if (dailyQuizHistoryRepository.existsByUserAndTargetDate(user, today)) {
            throw new CustomException(ErrorCode.ALREADY_SUBMITTED);
        }

        DailyQuiz quiz = dailyQuizRepository.findById(quizId)
                .orElseThrow(() -> new CustomException(ErrorCode.QUIZ_NOT_FOUND));

        boolean isCorrect = quiz.getCorrectAnswer().equals(answer);
        int rewardCoin = isCorrect ? QUIZ_REWARD_COIN : 0;

        // 이력 저장
        UserDailyQuizHistory history = UserDailyQuizHistory.builder()
                .user(user)
                .dailyQuiz(quiz)
                .selectedAnswer(answer)
                .isCorrect(isCorrect)
                .targetDate(today)
                .build();
        dailyQuizHistoryRepository.save(history);

        // 코인 지급
        if (isCorrect) {
            user.increaseCoinBalance(rewardCoin);
        }

        return DailyQuizSubmitResponse.of(
                isCorrect, quiz.getCorrectAnswer(), quiz.getExplanation(),
                rewardCoin, user.getCoinBalance());
    }

    // ════════════════════════════════════════
    //  4지선다 금융 퀴즈
    // ════════════════════════════════════════

    /**
     * 맵 화면의 4지선다 금융 퀴즈를 조회한다.
     */
    @Transactional(readOnly = true)
    public FinanceQuizResponse getFinanceQuiz() {
        FinanceQuiz quiz = financeQuizRepository.findOneRandom()
                .orElseThrow(() -> new CustomException(ErrorCode.QUIZ_NOT_FOUND));

        return FinanceQuizResponse.from(quiz);
    }

    /**
     * 4지선다 금융 퀴즈 정답을 제출한다.
     * 정답이면 1코인 지급 + 주사위 활성화.
     */
    @Transactional
    public FinanceQuizSubmitResponse submitFinanceQuiz(Long userId, Long quizId,
                                                       Integer selectedOption) {
        User user = findByUserId(userId);

        FinanceQuiz quiz = financeQuizRepository.findById(quizId)
                .orElseThrow(() -> new CustomException(ErrorCode.QUIZ_NOT_FOUND));

        boolean isCorrect = quiz.getCorrectOption().equals(selectedOption);
        int rewardCoin = isCorrect ? QUIZ_REWARD_COIN : 0;

        // 이력 저장
        UserFinanceQuizHistory history = UserFinanceQuizHistory.builder()
                .user(user)
                .financeQuiz(quiz)
                .selectedOption(selectedOption)
                .isCorrect(isCorrect)
                .targetDate(LocalDate.now())
                .build();
        financeQuizHistoryRepository.save(history);

        // 코인 지급
        if (isCorrect) {
            user.increaseCoinBalance(rewardCoin);
        }

        return FinanceQuizSubmitResponse.of(
                isCorrect, quiz.getCorrectOption(), quiz.getExplanation(),
                rewardCoin, user.getCoinBalance(), isCorrect);
    }

    // ── private helpers ──

    private User findByUserId(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    /** 날짜 기반 순환으로 오늘의 퀴즈를 결정한다. */
    private DailyQuiz selectTodaysDailyQuiz() {
        long totalCount = dailyQuizRepository.count();
        if (totalCount == 0) {
            throw new CustomException(ErrorCode.QUIZ_NOT_FOUND);
        }
        long offset = LocalDate.now().toEpochDay() % totalCount;
        return dailyQuizRepository.findByOffset(offset)
                .orElseThrow(() -> new CustomException(ErrorCode.QUIZ_NOT_FOUND));
    }
}
