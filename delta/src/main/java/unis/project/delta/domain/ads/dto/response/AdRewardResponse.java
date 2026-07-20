package unis.project.delta.domain.ads.dto.response;

public record AdRewardResponse(
        Integer bonusCoin,
        Integer coinBalance
) {
    public static AdRewardResponse of(Integer bonusCoin, Integer coinBalance) {
        return new AdRewardResponse(bonusCoin, coinBalance);
    }
}
