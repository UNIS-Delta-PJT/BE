package unis.project.delta.domain.item.dto.response;

import unis.project.delta.domain.item.entity.UserItem;

public record MyItemResponse(
        Long itemId,
        String name,
        String itemType,
        boolean isEquipped
) {
    public static MyItemResponse from(UserItem userItem) {
        return new MyItemResponse(
                userItem.getItem().getId(),
                userItem.getItem().getName(),
                userItem.getItem().getType().name(),
                userItem.getIsEquipped()
        );
    }
}
