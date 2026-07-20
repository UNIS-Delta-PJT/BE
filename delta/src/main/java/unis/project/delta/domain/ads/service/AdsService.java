package unis.project.delta.domain.ads.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import unis.project.delta.domain.ads.dto.response.AdRewardResponse;
import unis.project.delta.domain.ads.entity.AdRewardType;
import unis.project.delta.domain.ads.entity.UserAdReward;
import unis.project.delta.domain.ads.repository.UserAdRewardRepository;
import unis.project.delta.domain.mission.repository.DailyMissionRepository;
import unis.project.delta.domain.quiz.repository.UserFinanceQuizHistoryRepository;
import unis.project.delta.domain.user.entity.User;
import unis.project.delta.domain.user.repository.UserRepository;
import unis.project.delta.global.exception.CustomException;
import unis.project.delta.global.exception.ErrorCode;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AdsService {

    private static final int AD_BONUS_COIN = 1;

    private final UserAdRewardRepository userAdRewardRepository;
    private final DailyMissionRepository dailyMissionRepository;
    private final UserFinanceQuizHistoryRepository financeQuizHistoryRepository;
    private final UserRepository userRepository;

    /**
     * 광고 시청 후 보너스 코인을 지급한다.
     * 1) 해당 활동의 기본 보상을 받은 이력이 있는지 확인
     * 2) 광고 보상 중복 수령 여부 확인
     * 3) 보너스 코인(1) 지급
     */
    @Transactional
    public AdRewardResponse claimAdReward(Long userId, String rewardTypeStr) {
        User user = findByUserId(userId);
        AdRewardType rewardType = parseRewardType(rewardTypeStr);
        LocalDate today = LocalDate.now();

        // 해당 활동 달성 여부 확인
        validateActivityCompleted(user, rewardType, today);

        // 중복 수령 검사
        if (userAdRewardRepository.existsByUserAndRewardTypeAndTargetDate(user, rewardType, today)) {
            throw new CustomException(ErrorCode.ALREADY_REWARDED);
        }

        // 보상 수령 이력 저장
        UserAdReward adReward = UserAdReward.builder()
                .user(user)
                .rewardType(rewardType)
                .targetDate(today)
                .build();
        userAdRewardRepository.save(adReward);

        // 코인 지급
        user.increaseCoinBalance(AD_BONUS_COIN);

        return AdRewardResponse.of(AD_BONUS_COIN, user.getCoinBalance());
    }

    // ── private helpers ──

    private User findByUserId(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    private AdRewardType parseRewardType(String value) {
        try {
            return AdRewardType.valueOf(value);
        } catch (IllegalArgumentException e) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }
    }

    /** 광고 보상의 전제 조건이 되는 활동이 오늘 달성되었는지 확인한다. */
    private void validateActivityCompleted(User user, AdRewardType type, LocalDate today) {
        boolean completed = switch (type) {
            case EXPENSE_RECORD -> dailyMissionRepository.findByUserAndTargetDate(user, today)
                    .map(m -> m.getIsExpenseDone())
                    .orElse(false);
            case FINANCE_QUIZ -> financeQuizHistoryRepository
                    .existsByUserAndTargetDateAndIsCorrectTrue(user, today);
        };

        if (!completed) {
            throw new CustomException(ErrorCode.MISSION_NOT_COMPLETED);
        }
    }
}
