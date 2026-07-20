package unis.project.delta.domain.item.dto.response;

import unis.project.delta.domain.item.entity.Item;

public record BuyItemResponse(
        Long itemId,
        String itemName,
        Integer price,
        Integer coinBalance
) {
    public static BuyItemResponse of(Item item, Integer coinBalance) {
        return new BuyItemResponse(
                item.getId(),
                item.getName(),
                item.getPrice(),
                coinBalance
        );
    }
}
