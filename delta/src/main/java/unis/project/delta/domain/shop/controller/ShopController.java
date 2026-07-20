package unis.project.delta.domain.shop.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import unis.project.delta.domain.shop.dto.response.CoinPackageListResponse;
import unis.project.delta.domain.shop.service.ShopService;
import unis.project.delta.global.response.ApiResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/shop")
public class ShopController {

    private final ShopService shopService;

    /**
     * 코인 패키지 리스트 조회.
     */
    @GetMapping("/coins")
    public ResponseEntity<ApiResponse<CoinPackageListResponse>> getCoinPackages(
            @AuthenticationPrincipal Long userId) {

        CoinPackageListResponse response = shopService.getCoinPackages(userId);
        return ResponseEntity.ok(ApiResponse.success(response, "코인 패키지 조회 성공"));
    }
}
