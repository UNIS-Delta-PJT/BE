package unis.project.delta.domain.item.dto.response;

import unis.project.delta.domain.item.entity.Item;

public record ShopItemResponse(
        Long itemId,
        String name,
        Integer price,
        String itemType,
        boolean isOwned
) {
    public static ShopItemResponse of(Item item, boolean isOwned) {
        return new ShopItemResponse(
                item.getId(),
                item.getName(),
                item.getPrice(),
                item.getType().name(),
                isOwned
        );
    }
}
