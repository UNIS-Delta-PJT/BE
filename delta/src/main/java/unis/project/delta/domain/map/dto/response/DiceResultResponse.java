package unis.project.delta.domain.map.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import unis.project.delta.domain.map.policy.MapEventPolicy;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record DiceResultResponse(
        int diceResult,
        int previousPosition,
        int landedPosition,
        int finalPosition,
        EventResponse event
) {
    public record EventResponse(
            String eventType,
            String description,
            Integer rewardCoin,
            Integer coinBalance
    ) {}

    public static DiceResultResponse of(int diceResult, int previousPosition,
                                        int landedPosition, MapEventPolicy.MapEvent mapEvent,
                                        Integer coinBalance) {
        Integer reward = mapEvent.rewardCoin() > 0 ? mapEvent.rewardCoin() : null;
        Integer balance = mapEvent.rewardCoin() > 0 ? coinBalance : null;

        EventResponse event = new EventResponse(
                mapEvent.eventType().name(), mapEvent.description(), reward, balance);

        return new DiceResultResponse(
                diceResult, previousPosition, landedPosition,
                mapEvent.finalPosition(), event);
    }
}
