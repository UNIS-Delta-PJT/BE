package unis.project.delta.domain.mission.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import unis.project.delta.domain.mission.dto.response.*;
import unis.project.delta.domain.mission.entity.DailyMission;
import unis.project.delta.domain.mission.entity.DailyMissionType;
import unis.project.delta.domain.mission.repository.DailyMissionRepository;
import unis.project.delta.domain.user.entity.User;
import unis.project.delta.domain.user.repository.UserRepository;
import unis.project.delta.global.exception.CustomException;
import unis.project.delta.global.exception.ErrorCode;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MissionService {

    private static final int MISSION_REWARD_COIN = 1;

    private final DailyMissionRepository dailyMissionRepository;
    private final UserRepository userRepository;

    /**
     * 지정 기간 내 출석체크 현황을 조회한다.
     * 기간 내 각 날짜의 출석 여부와 현재 연속 출석 일수를 반환한다.
     */
    @Transactional(readOnly = true)
    public AttendanceResponse getAttendance(Long userId, LocalDate startDate, LocalDate endDate) {
        User user = findByUserId(userId);

        // 기간 내 DailyMission 조회 → targetDate를 키로 Map 변환
        Map<LocalDate, DailyMission> missionMap = dailyMissionRepository
                .findByUserAndTargetDateBetween(user, startDate, endDate)
                .stream()
                .collect(Collectors.toMap(DailyMission::getTargetDate, Function.identity()));

        // 기간 내 모든 날짜에 대해 출석 여부 생성
        List<AttendanceDetailResponse> attendances = startDate.datesUntil(endDate.plusDays(1))
                .map(date -> {
                    DailyMission mission = missionMap.get(date);
                    boolean attended = mission != null && mission.getIsAttendanceDone();
                    return AttendanceDetailResponse.of(date, attended);
                })
                .toList();

        return AttendanceResponse.of(user.getContinuousAttendance(), attendances);
    }

    /**
     * 오늘의 출석체크를 기록한다.
     * 이미 출석한 경우 409 Conflict를 반환한다.
     * 어제 출석 여부에 따라 연속 출석 일수를 갱신한다.
     */
    @Transactional
    public AttendanceCheckResponse checkAttendance(Long userId) {
        User user = findByUserId(userId);
        LocalDate today = LocalDate.now();

        // 오늘의 DailyMission 조회 또는 생성
        DailyMission todayMission = getOrCreateDailyMission(user, today);

        // 이미 출석한 경우
        if (todayMission.getIsAttendanceDone()) {
            throw new CustomException(ErrorCode.ALREADY_ATTENDED);
        }

        // 출석 처리
        todayMission.completeAttendance();

        // 연속 출석 갱신: 어제 출석했으면 연속 +1, 아니면 1로 리셋
        updateContinuousAttendance(user, today);

        return AttendanceCheckResponse.of(user.getContinuousAttendance(), today);
    }

    /**
     * 오늘의 미션 3종 달성 및 리워드 수령 상태를 조회한다.
     * 오늘의 DailyMission이 없으면 새로 생성하여 반환한다.
     */
    @Transactional
    public DailyMissionResponse getDailyMission(Long userId) {
        User user = findByUserId(userId);
        LocalDate today = LocalDate.now();

        DailyMission todayMission = getOrCreateDailyMission(user, today);

        return DailyMissionResponse.from(todayMission);
    }

    /**
     * 달성된 미션의 리워드(1코인)를 수령한다.
     * 미션이 달성되지 않았거나 이미 수령한 경우 예외를 발생시킨다.
     */
    @Transactional
    public MissionRewardResponse claimReward(Long userId, DailyMissionType missionType) {
        User user = findByUserId(userId);
        LocalDate today = LocalDate.now();

        DailyMission todayMission = getOrCreateDailyMission(user, today);

        // 미션 달성 여부 확인
        if (!todayMission.isDone(missionType)) {
            throw new CustomException(ErrorCode.MISSION_NOT_COMPLETED);
        }

        // 이미 리워드를 수령했는지 확인
        if (todayMission.isRewarded(missionType)) {
            throw new CustomException(ErrorCode.ALREADY_REWARDED);
        }

        // 리워드 수령 처리
        todayMission.reward(missionType);
        user.increaseCoinBalance(MISSION_REWARD_COIN);

        return MissionRewardResponse.of(missionType.name(), MISSION_REWARD_COIN, user.getCoinBalance());
    }

    // ── private helpers ──

    private User findByUserId(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    /**
     * 오늘의 DailyMission을 조회하고, 없으면 새로 생성한다.
     */
    private DailyMission getOrCreateDailyMission(User user, LocalDate date) {
        return dailyMissionRepository.findByUserAndTargetDate(user, date)
                .orElseGet(() -> dailyMissionRepository.save(
                        DailyMission.builder()
                                .user(user)
                                .targetDate(date)
                                .build()
                ));
    }

    /**
     * 연속 출석 일수를 갱신한다.
     * 어제 출석을 했으면 연속 출석 +1, 하지 않았으면 1로 리셋한다.
     */
    private void updateContinuousAttendance(User user, LocalDate today) {
        LocalDate yesterday = today.minusDays(1);

        boolean attendedYesterday = dailyMissionRepository.findByUserAndTargetDate(user, yesterday)
                .map(DailyMission::getIsAttendanceDone)
                .orElse(false);

        if (!attendedYesterday) {
            user.resetContinuousAttendance();
        }
        user.increaseContinuousAttendance();
    }
}
