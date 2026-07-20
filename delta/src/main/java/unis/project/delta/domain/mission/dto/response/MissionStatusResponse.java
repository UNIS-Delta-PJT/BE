package unis.project.delta.domain.mission.dto.response;

public record MissionStatusResponse(
        String missionType,
        boolean isDone,
        boolean isRewarded
) {
    public static MissionStatusResponse of(String missionType, boolean isDone, boolean isRewarded) {
        return new MissionStatusResponse(missionType, isDone, isRewarded);
    }
}
