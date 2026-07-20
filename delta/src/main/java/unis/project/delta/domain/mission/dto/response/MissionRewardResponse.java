package unis.project.delta.domain.mission.dto.response;

public record MissionRewardResponse(
        String missionType,
        Integer rewardCoin,
        Integer coinBalance
) {
    public static MissionRewardResponse of(String missionType, Integer rewardCoin, Integer coinBalance) {
        return new MissionRewardResponse(missionType, rewardCoin, coinBalance);
    }
}
