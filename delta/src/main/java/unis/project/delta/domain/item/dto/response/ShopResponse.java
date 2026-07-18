package unis.project.delta.domain.item.dto.response;

import java.util.List;

public record ShopResponse(
        Integer coinBalance,
        List<ShopItemResponse> items
) {
    public static ShopResponse of(Integer coinBalance, List<ShopItemResponse> items) {
        return new ShopResponse(coinBalance, items);
    }
}
