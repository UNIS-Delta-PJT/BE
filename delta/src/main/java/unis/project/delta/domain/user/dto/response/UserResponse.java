package unis.project.delta.domain.user.dto.response;

import unis.project.delta.domain.item.entity.UserItem;
import unis.project.delta.domain.user.entity.User;

import java.util.List;

public record UserResponse(
        Long userId,
        Integer coinBalance,
        Integer continuousAttendance,
        Integer mapPosition,
        CharacterResponse character,
        NotificationResponse notification,
        List<EquippedItemResponse> equippedItems
) {
    public static UserResponse from(User user, List<UserItem> equippedUserItems) {
        return new UserResponse(
                user.getId(),
                user.getCoinBalance(),
                user.getContinuousAttendance(),
                user.getMapPosition(),
                CharacterResponse.from(user),
                NotificationResponse.from(user),
                equippedUserItems.stream()
                        .map(EquippedItemResponse::from)
                        .toList()
        );
    }
}
