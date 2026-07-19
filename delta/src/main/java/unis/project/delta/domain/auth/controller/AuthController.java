package unis.project.delta.domain.auth.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import unis.project.delta.domain.auth.dto.request.KakaoLoginRequest;
import unis.project.delta.domain.auth.dto.response.LoginResponse;
import unis.project.delta.domain.auth.dto.response.TokenReissueResponse;
import unis.project.delta.domain.auth.service.AuthService;
import unis.project.delta.domain.auth.service.AuthService.LoginResult;
import unis.project.delta.domain.auth.service.AuthService.ReissueResult;
import unis.project.delta.global.response.ApiResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    /**
     * 카카오 로그인 / 회원가입.
     * 기존 유저: 200 OK, 신규 유저: 201 Created.
     * Refresh Token은 HttpOnly 쿠키로 반환한다.
     */
    @PostMapping("/kakao/login")
    public ResponseEntity<ApiResponse<LoginResponse>> kakaoLogin(
            @Valid @RequestBody KakaoLoginRequest request) {

        LoginResult result = authService.kakaoLogin(request.getKakaoAccessToken());

        ResponseCookie refreshCookie = createRefreshTokenCookie(result.refreshToken());

        HttpStatus status = result.isNewUser() ? HttpStatus.CREATED : HttpStatus.OK;

        return ResponseEntity.status(status)
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(ApiResponse.success(result.loginResponse(), "카카오 로그인 성공"));
    }

    /**
     * 로그아웃.
     * Refresh Token을 폐기하고 쿠키를 삭제한다.
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @CookieValue("refreshToken") String refreshToken) {

        authService.logout(refreshToken);

        ResponseCookie deleteCookie = deleteRefreshTokenCookie();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
                .body(ApiResponse.success("성공적으로 로그아웃 되었습니다."));
    }

    /**
     * 토큰 재발급.
     * Refresh Token을 검증하고 새로운 Access Token을 발급한다.
     * Refresh Token이 로테이션되면 새 쿠키를 반환한다.
     */
    @PostMapping("/reissue")
    public ResponseEntity<ApiResponse<TokenReissueResponse>> reissue(
            @CookieValue("refreshToken") String refreshToken) {

        ReissueResult result = authService.reissue(refreshToken);

        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.ok();

        if (result.rotated()) {
            ResponseCookie newCookie = createRefreshTokenCookie(result.newRefreshToken());
            responseBuilder.header(HttpHeaders.SET_COOKIE, newCookie.toString());
        }

        return responseBuilder.body(ApiResponse.success(result.reissueResponse(), "토큰 재발급 성공"));
    }

    // ── cookie helpers ──

    private ResponseCookie createRefreshTokenCookie(String token) {
        return ResponseCookie.from("refreshToken", token)
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .build();
    }

    private ResponseCookie deleteRefreshTokenCookie() {
        return ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(0)
                .build();
    }
}
