package unis.project.delta.domain.item.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import unis.project.delta.domain.item.dto.request.EquipItemRequest;
import unis.project.delta.domain.item.dto.response.BuyItemResponse;
import unis.project.delta.domain.item.dto.response.MyItemListResponse;
import unis.project.delta.domain.item.dto.response.ShopResponse;
import unis.project.delta.domain.item.service.ItemService;
import unis.project.delta.global.response.ApiResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/items")
public class ItemController {

    private final ItemService itemService;

    /**
     * 코인 상점 아이템 리스트 및 가격 조회.
     */
    @GetMapping("/shop")
    public ResponseEntity<ApiResponse<ShopResponse>> getShopItems(
            @AuthenticationPrincipal Long userId) {

        ShopResponse response = itemService.getShopItems(userId);
        return ResponseEntity.ok(ApiResponse.success(response, "상점 아이템 조회 성공"));
    }

    /**
     * 아이템 구매.
     */
    @PostMapping("/{itemId}/buy")
    public ResponseEntity<ApiResponse<BuyItemResponse>> buyItem(
            @AuthenticationPrincipal Long userId,
            @PathVariable("itemId") Long itemId) {

        BuyItemResponse response = itemService.buyItem(userId, itemId);
        return ResponseEntity.ok(ApiResponse.success(response, "아이템 구매 성공"));
    }

    /**
     * 내 아이템 리스트 조회.
     */
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<MyItemListResponse>> getMyItems(
            @AuthenticationPrincipal Long userId) {

        MyItemListResponse response = itemService.getMyItems(userId);
        return ResponseEntity.ok(ApiResponse.success(response, "내 아이템 조회 성공"));
    }

    /**
     * 아이템 착용/벗기.
     */
    @PatchMapping("/my/{itemId}/equip")
    public ResponseEntity<ApiResponse<Void>> equipItem(
            @AuthenticationPrincipal Long userId,
            @PathVariable("itemId") Long itemId,
            @Valid @RequestBody EquipItemRequest request) {

        itemService.equipItem(userId, itemId, request.getEquip());
        return ResponseEntity.ok(ApiResponse.success("아이템 착용 상태 변경 성공"));
    }
}
