package unis.project.delta.domain.mission.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import unis.project.delta.domain.mission.dto.response.AttendanceCheckResponse;
import unis.project.delta.domain.mission.dto.response.AttendanceResponse;
import unis.project.delta.domain.mission.dto.response.DailyMissionResponse;
import unis.project.delta.domain.mission.dto.response.MissionRewardResponse;
import unis.project.delta.domain.mission.entity.DailyMissionType;
import unis.project.delta.domain.mission.service.MissionService;
import unis.project.delta.global.exception.CustomException;
import unis.project.delta.global.exception.ErrorCode;
import unis.project.delta.global.response.ApiResponse;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/missions")
public class MissionController {

    private final MissionService missionService;

    /**
     * 출석체크 현황 조회.
     * 지정 기간 내 날짜별 출석 여부와 현재 연속 출석 일수를 반환한다.
     */
    @GetMapping("/attendance")
    public ResponseEntity<ApiResponse<AttendanceResponse>> getAttendance(
            @AuthenticationPrincipal Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        AttendanceResponse response = missionService.getAttendance(userId, startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(response, "출석체크 현황 조회 성공"));
    }

    /**
     * 오늘의 출석체크 기록.
     */
    @PostMapping("/attendance")
    public ResponseEntity<ApiResponse<AttendanceCheckResponse>> checkAttendance(
            @AuthenticationPrincipal Long userId) {

        AttendanceCheckResponse response = missionService.checkAttendance(userId);
        return ResponseEntity.ok(ApiResponse.success(response, "출석체크 완료"));
    }

    /**
     * 오늘의 미션 달성 및 리워드 수령 상태 조회.
     */
    @GetMapping("/daily")
    public ResponseEntity<ApiResponse<DailyMissionResponse>> getDailyMission(
            @AuthenticationPrincipal Long userId) {

        DailyMissionResponse response = missionService.getDailyMission(userId);
        return ResponseEntity.ok(ApiResponse.success(response, "오늘의 미션 조회 성공"));
    }

    /**
     * 달성 미션 리워드(코인) 받기.
     */
    @PostMapping("/daily/{missionType}/reward")
    public ResponseEntity<ApiResponse<MissionRewardResponse>> claimReward(
            @AuthenticationPrincipal Long userId,
            @PathVariable("missionType") String missionTypeStr) {

        DailyMissionType missionType = parseMissionType(missionTypeStr);
        MissionRewardResponse response = missionService.claimReward(userId, missionType);
        return ResponseEntity.ok(ApiResponse.success(response, "미션 리워드 수령 성공"));
    }

    // ── private helpers ──

    private DailyMissionType parseMissionType(String value) {
        try {
            return DailyMissionType.valueOf(value);
        } catch (IllegalArgumentException e) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }
    }
}
