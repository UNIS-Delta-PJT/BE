package unis.project.delta.domain.map.policy;

import java.util.Map;
import java.util.Set;

/**
 * 맵 칸 번호별 이벤트 규칙.
 * 기능명세서에 정의된 이벤트 테이블을 코드로 표현한다.
 */
public final class MapEventPolicy {

    private MapEventPolicy() {}

    private static final int MAX_POSITION = 100;
    private static final int TREASURE_REWARD_COIN = 3;

    /** 🎁 보물상자 */
    private static final Set<Integer> TREASURE_POSITIONS = Set.of(
            2, 7, 16, 20, 23, 30, 37, 43, 47, 50,
            52, 56, 61, 65, 70, 71, 78, 83, 92, 97
    );

    /** 💀 처음으로 */
    private static final Set<Integer> RESET_POSITIONS = Set.of(10, 22, 86);

    /** 🔙 N칸 뒤로 — position → 뒤로 갈 칸 수 */
    private static final Map<Integer, Integer> BACK_POSITIONS = Map.of(
            67, 1,
            55, 2, 91, 2,
            27, 3,
            40, 5, 77, 5,
            75, 6
    );

    public static MapEvent resolve(int position) {
        if (position >= MAX_POSITION) {
            return new MapEvent(MapEventType.FINISH, "도착! 축하합니다!", 0, MAX_POSITION);
        }
        if (TREASURE_POSITIONS.contains(position)) {
            return new MapEvent(MapEventType.TREASURE, "보물상자를 발견했습니다!",
                    TREASURE_REWARD_COIN, position);
        }
        if (RESET_POSITIONS.contains(position)) {
            return new MapEvent(MapEventType.RESET, "처음으로 돌아갑니다!", 0, 1);
        }
        if (BACK_POSITIONS.containsKey(position)) {
            int backSteps = BACK_POSITIONS.get(position);
            int finalPos = Math.max(1, position - backSteps);
            return new MapEvent(MapEventType.BACK,
                    backSteps + "칸 뒤로 이동합니다!", 0, finalPos);
        }
        return new MapEvent(MapEventType.NORMAL, "일반 칸입니다.", 0, position);
    }

    /** 이벤트 판정 결과 */
    public record MapEvent(
            MapEventType eventType,
            String description,
            int rewardCoin,
            int finalPosition
    ) {}
}
