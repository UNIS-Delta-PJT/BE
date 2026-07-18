package unis.project.delta.domain.user.dto.response;

import unis.project.delta.domain.item.entity.UserItem;

public record EquippedItemResponse(
        Long itemId,
        String itemType
) {
    public static EquippedItemResponse from(UserItem userItem) {
        return new EquippedItemResponse(
                userItem.getItem().getId(),
                userItem.getItem().getType().name()
        );
    }
}
