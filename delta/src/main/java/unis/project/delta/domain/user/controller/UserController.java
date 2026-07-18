package unis.project.delta.domain.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import unis.project.delta.domain.user.dto.request.CharacterUpdateRequest;
import unis.project.delta.domain.user.dto.request.NotificationUpdateRequest;
import unis.project.delta.domain.user.dto.response.UserResponse;
import unis.project.delta.domain.user.service.UserService;
import unis.project.delta.global.response.ApiResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    /**
     * 내 정보 조회.
     * 캐릭터, 알림 설정, 장착 아이템을 포함한 사용자 전체 정보를 반환한다.
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getMyInfo(
            @AuthenticationPrincipal Long userId) {

        UserResponse response = userService.getMyInfo(userId);
        return ResponseEntity.ok(ApiResponse.success(response, "내 정보 조회 성공"));
    }

    /**
     * 캐릭터 설정.
     * 닉네임, 몸통 색상, 눈 모양을 부분 변경한다.
     */
    @PatchMapping("/character")
    public ResponseEntity<ApiResponse<Void>> updateCharacter(
            @AuthenticationPrincipal Long userId,
            @RequestBody CharacterUpdateRequest request) {

        userService.updateCharacter(userId, request);
        return ResponseEntity.ok(ApiResponse.success("캐릭터 설정 성공"));
    }

    /**
     * 알림 설정 변경.
     * 전체 푸시 알림 수신 여부와 야간 알림 방해금지를 설정한다.
     */
    @PatchMapping("/notifications")
    public ResponseEntity<ApiResponse<Void>> updateNotification(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody NotificationUpdateRequest request) {

        userService.updateNotification(userId, request);
        return ResponseEntity.ok(ApiResponse.success("알림 설정이 성공적으로 변경되었습니다."));
    }
}
