package unis.project.delta.domain.map.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import unis.project.delta.domain.map.dto.response.DiceResultResponse;
import unis.project.delta.domain.map.service.MapService;
import unis.project.delta.global.response.ApiResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/map")
public class MapController {

    private final MapService mapService;

    /**
     * 주사위 굴리기 실행 및 맵 위치 이동.
     */
    @PostMapping("/dice")
    public ResponseEntity<ApiResponse<DiceResultResponse>> rollDice(
            @AuthenticationPrincipal Long userId) {

        DiceResultResponse response = mapService.rollDice(userId);
        return ResponseEntity.ok(ApiResponse.success(response, "주사위 굴리기 성공"));
    }
}
