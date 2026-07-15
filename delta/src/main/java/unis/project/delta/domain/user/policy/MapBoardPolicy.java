package unis.project.delta.domain.user.policy;

import unis.project.delta.domain.user.constant.SpaceType;
import java.util.HashMap;
import java.util.Map;

public class MapBoardPolicy {

    private static final Map<Integer, SpaceType> BOARD = new HashMap<>();

    static {
        // 아무것도 안 적힌 칸은 NORMAL로 처리
        BOARD.put(2, SpaceType.REWARD);
        BOARD.put(7, SpaceType.REWARD);
        BOARD.put(10, SpaceType.RESET);
        BOARD.put(16, SpaceType.REWARD);
        BOARD.put(20, SpaceType.REWARD);
        BOARD.put(22, SpaceType.RESET);
        BOARD.put(23, SpaceType.REWARD);
        BOARD.put(27, SpaceType.BACK3);
        BOARD.put(30, SpaceType.REWARD);
        BOARD.put(37, SpaceType.REWARD);
        BOARD.put(40, SpaceType.BACK5);
        BOARD.put(43, SpaceType.REWARD);
        BOARD.put(47, SpaceType.REWARD);
        BOARD.put(50, SpaceType.REWARD);
        BOARD.put(52, SpaceType.REWARD);
        BOARD.put(55, SpaceType.BACK2);
        BOARD.put(56, SpaceType.REWARD);
        BOARD.put(61, SpaceType.REWARD);
        BOARD.put(65, SpaceType.REWARD);
        BOARD.put(67, SpaceType.BACK1);
        BOARD.put(70, SpaceType.REWARD);
        BOARD.put(71, SpaceType.REWARD);
        BOARD.put(75, SpaceType.BACK6);
        BOARD.put(77, SpaceType.BACK5);
        BOARD.put(78, SpaceType.REWARD);
        BOARD.put(83, SpaceType.REWARD);
        BOARD.put(86, SpaceType.RESET);
        BOARD.put(91, SpaceType.BACK2);
        BOARD.put(92, SpaceType.REWARD);
        BOARD.put(97, SpaceType.REWARD);
        BOARD.put(100, SpaceType.ARRIVAL);
    }

    public static SpaceType getSpaceType(int position) {
        // BOARD에 세팅된 칸이면 그 타입을 반환하고, 세팅 안 된 칸이면 기본값인 NORMAL 반환
        return BOARD.getOrDefault(position, SpaceType.NORMAL);
    }
}