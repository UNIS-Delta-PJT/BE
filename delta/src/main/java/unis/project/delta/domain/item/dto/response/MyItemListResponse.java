package unis.project.delta.domain.item.dto.response;

import unis.project.delta.domain.item.entity.UserItem;

import java.util.List;

public record MyItemListResponse(
        List<MyItemResponse> items
) {
    public static MyItemListResponse from(List<UserItem> userItems) {
        return new MyItemListResponse(
                userItems.stream().map(MyItemResponse::from).toList()
        );
    }
}
