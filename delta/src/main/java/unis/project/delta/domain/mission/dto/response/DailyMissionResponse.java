package unis.project.delta.domain.mission.dto.response;

import unis.project.delta.domain.mission.entity.DailyMission;

import java.util.List;

public record DailyMissionResponse(
        String targetDate,
        List<MissionStatusResponse> missions
) {
    public static DailyMissionResponse from(DailyMission dailyMission) {
        List<MissionStatusResponse> missions = List.of(
                MissionStatusResponse.of("ATTENDANCE",
                        dailyMission.getIsAttendanceDone(),
                        dailyMission.getIsAttendanceRewarded()),
                MissionStatusResponse.of("EXPENSE_RECORD",
                        dailyMission.getIsExpenseDone(),
                        dailyMission.getIsExpenseRewarded()),
                MissionStatusResponse.of("DICE",
                        dailyMission.getIsDiceDone(),
                        dailyMission.getIsDiceRewarded())
        );
        return new DailyMissionResponse(dailyMission.getTargetDate().toString(), missions);
    }
}
