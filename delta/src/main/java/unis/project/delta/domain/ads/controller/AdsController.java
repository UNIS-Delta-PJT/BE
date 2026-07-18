package unis.project.delta.domain.ads.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import unis.project.delta.domain.ads.dto.request.AdRewardRequest;
import unis.project.delta.domain.ads.dto.response.AdRewardResponse;
import unis.project.delta.domain.ads.service.AdsService;
import unis.project.delta.global.response.ApiResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/ads")
public class AdsController {

    private final AdsService adsService;

    /**
     * 광고 시청 후 코인 2배 받기.
     */
    @PostMapping("/reward")
    public ResponseEntity<ApiResponse<AdRewardResponse>> claimAdReward(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody AdRewardRequest request) {

        AdRewardResponse response = adsService.claimAdReward(userId, request.getRewardType());
        return ResponseEntity.ok(ApiResponse.success(response, "광고 보상 지급 성공"));
    }
}
