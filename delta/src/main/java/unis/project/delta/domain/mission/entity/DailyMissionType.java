package unis.project.delta.domain.mission.entity;

/**
 * 일일 미션 종류.
 * POST /api/v1/missions/daily/{missionType}/reward 에서 path variable로 사용된다.
 */
public enum DailyMissionType {
    ATTENDANCE,
    EXPENSE_RECORD,
    DICE
}
