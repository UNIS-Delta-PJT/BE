package unis.project.delta.domain.map.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import unis.project.delta.domain.map.dto.response.DiceResultResponse;
import unis.project.delta.domain.map.policy.MapEventPolicy;
import unis.project.delta.domain.mission.entity.DailyMission;
import unis.project.delta.domain.mission.repository.DailyMissionRepository;
import unis.project.delta.domain.quiz.repository.UserFinanceQuizHistoryRepository;
import unis.project.delta.domain.user.entity.User;
import unis.project.delta.domain.user.repository.UserRepository;
import unis.project.delta.global.exception.CustomException;
import unis.project.delta.global.exception.ErrorCode;

import java.time.LocalDate;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class MapService {

    private final UserRepository userRepository;
    private final UserFinanceQuizHistoryRepository financeQuizHistoryRepository;
    private final DailyMissionRepository dailyMissionRepository;

    /**
     * 주사위를 굴려 맵을 이동한다.
     * 1) 금융 퀴즈 정답 여부 확인 → 미통과 시 403
     * 2) 주사위(1~6) 결과로 위치 이동
     * 3) 도착 칸의 이벤트(보상/패널티) 적용
     * 4) 주사위 미션 달성 처리
     */
    @Transactional
    public DiceResultResponse rollDice(Long userId) {
        User user = findByUserId(userId);
        LocalDate today = LocalDate.now();

        // 1. 금융 퀴즈 통과 여부 확인
        boolean quizPassed = financeQuizHistoryRepository
                .existsByUserAndTargetDateAndIsCorrectTrue(user, today);
        if (!quizPassed) {
            throw new CustomException(ErrorCode.DICE_NOT_ENABLED);
        }

        // 2. 주사위 굴리기
        int diceResult = ThreadLocalRandom.current().nextInt(1, 7);
        int previousPosition = user.getMapPosition();
        int landedPosition = previousPosition + diceResult;

        // 3. 이벤트 판정
        MapEventPolicy.MapEvent event = MapEventPolicy.resolve(landedPosition);

        // 4. 위치 및 보상 적용
        applyMapEvent(user, event);

        // 5. 주사위 미션 달성 처리
        completeDiceMission(user, today);

        return DiceResultResponse.of(
                diceResult, previousPosition, landedPosition,
                event, user.getCoinBalance());
    }

    // ── private helpers ──

    private User findByUserId(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    private void applyMapEvent(User user, MapEventPolicy.MapEvent event) {
        switch (event.eventType()) {
            case FINISH -> {
                user.resetMapPosition();          // 100 도착 시 1로 리셋 (한 바퀴 완료)
                user.increaseCoinBalance(event.rewardCoin());
            }
            case TREASURE -> {
                user.increaseCoinBalance(event.rewardCoin());
                setUserPosition(user, event.finalPosition());
            }
            case RESET -> user.resetMapPosition();
            case BACK -> setUserPosition(user, event.finalPosition());
            case NORMAL -> setUserPosition(user, event.finalPosition());
        }
    }

    private void setUserPosition(User user, int position) {
        // 절대 위치 세팅: 현재 위치와의 차이를 계산하여 이동
        int diff = position - user.getMapPosition();
        if (diff > 0) {
            user.increaseMapPosition(diff);
        } else if (diff < 0) {
            user.decreaseMapPosition(Math.abs(diff));
        }
    }

    private void completeDiceMission(User user, LocalDate date) {
        DailyMission mission = dailyMissionRepository.findByUserAndTargetDate(user, date)
                .orElseGet(() -> dailyMissionRepository.save(
                        DailyMission.builder()
                                .user(user)
                                .targetDate(date)
                                .build()
                ));
        mission.completeDice();
    }
}
