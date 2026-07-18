package unis.project.delta.domain.group.dto.response;

import unis.project.delta.domain.item.entity.UserItem;
import unis.project.delta.domain.user.entity.User;

import java.util.List;

public record GroupMemberResponse(
        Long userId,
        String nickname,
        String bodyColor,
        String eyeShape,
        Integer mapPosition,
        List<EquippedItemDto> equippedItems
) {
    public record EquippedItemDto(Long itemId, String itemType) {}

    public static GroupMemberResponse from(User user, List<UserItem> equipped) {
        List<EquippedItemDto> items = equipped.stream()
                .map(ui -> new EquippedItemDto(ui.getItem().getId(), ui.getItem().getType().name()))
                .toList();
        return new GroupMemberResponse(
                user.getId(),
                user.getNickname(),
                user.getBodyColor().name(),
                user.getEyeShape().name(),
                user.getMapPosition(),
                items
        );
    }
}
