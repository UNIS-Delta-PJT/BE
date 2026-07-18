package unis.project.delta.domain.auth.service;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import unis.project.delta.domain.auth.dto.response.LoginResponse;
import unis.project.delta.domain.auth.dto.response.TokenReissueResponse;
import unis.project.delta.domain.auth.entity.RefreshToken;
import unis.project.delta.domain.user.entity.User;
import unis.project.delta.domain.auth.repository.RefreshTokenRepository;
import unis.project.delta.domain.user.repository.UserRepository;
import unis.project.delta.global.config.jwt.JwtTokenProvider;
import unis.project.delta.global.exception.CustomException;
import unis.project.delta.global.exception.ErrorCode;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final KakaoOAuthClient kakaoOAuthClient;

    /**
     * 카카오 로그인 / 회원가입 처리.
     * 신규 사용자면 User를 생성하고, 기존 사용자면 조회한다.
     * DELTA Access Token과 Refresh Token을 발급하여 반환한다.
     */
    @Transactional
    public LoginResult kakaoLogin(String kakaoAccessToken) {
        // 1. 카카오 API로 사용자 고유 ID 조회
        String oauthId = kakaoOAuthClient.getKakaoUserId(kakaoAccessToken);

        // 2. 기존 사용자 조회 또는 신규 생성
        boolean isNewUser = false;
        User user = userRepository.findByOauthId(oauthId).orElse(null);

        if (user == null) {
            user = User.builder()
                    .oauthId(oauthId)
                    .build();
            userRepository.save(user);
            isNewUser = true;
        }

        // 3. JWT 토큰 발급
        String accessToken = jwtTokenProvider.createAccessToken(user.getId());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getId());
        long expiresIn = jwtTokenProvider.getAccessTokenExpirationInSeconds();

        // 4. Refresh Token 저장 (기존 토큰이 있으면 교체)
        saveOrUpdateRefreshToken(user.getId(), refreshToken);

        // 5. 응답 생성
        LoginResponse loginResponse = LoginResponse.from(accessToken, expiresIn, isNewUser, user.getId());
        return new LoginResult(loginResponse, refreshToken, isNewUser);
    }

    /**
     * 로그아웃 처리.
     * Refresh Token을 DB에서 삭제한다.
     */
    @Transactional
    public void logout(String refreshTokenValue) {
        // 1. Refresh Token 유효성 검증
        Long userId = validateAndExtractUserId(refreshTokenValue);

        // 2. 저장된 Refresh Token과 일치하는지 확인
        RefreshToken storedToken = refreshTokenRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_REFRESH_TOKEN));

        if (!storedToken.getToken().equals(refreshTokenValue)) {
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        // 3. Refresh Token 삭제
        refreshTokenRepository.deleteByUserId(userId);
    }

    /**
     * 토큰 재발급 처리.
     * Refresh Token을 검증하고 새로운 Access Token을 발급한다.
     * 필요시 Refresh Token도 함께 로테이션한다.
     */
    @Transactional
    public ReissueResult reissue(String refreshTokenValue) {
        // 1. Refresh Token 유효성 검증 및 userId 추출
        Long userId = validateAndExtractUserId(refreshTokenValue);

        // 2. DB에 저장된 토큰과 일치하는지 확인
        RefreshToken storedToken = refreshTokenRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_REFRESH_TOKEN));

        if (!storedToken.getToken().equals(refreshTokenValue)) {
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        // 3. 사용자 존재 확인
        userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 4. 새로운 Access Token 발급
        String newAccessToken = jwtTokenProvider.createAccessToken(userId);
        long expiresIn = jwtTokenProvider.getAccessTokenExpirationInSeconds();

        // 5. Refresh Token 로테이션 여부 판단
        boolean rotated = false;
        String newRefreshToken = null;

        if (jwtTokenProvider.shouldRotateRefreshToken(refreshTokenValue)) {
            newRefreshToken = jwtTokenProvider.createRefreshToken(userId);
            storedToken.rotateToken(newRefreshToken);
            rotated = true;
        }

        // 6. 응답 생성
        TokenReissueResponse response = TokenReissueResponse.from(newAccessToken, expiresIn, rotated);
        return new ReissueResult(response, newRefreshToken, rotated);
    }

    // ── private helpers ──

    private Long validateAndExtractUserId(String refreshTokenValue) {
        try {
            jwtTokenProvider.validateRefreshToken(refreshTokenValue);
            return jwtTokenProvider.getUserIdFromToken(refreshTokenValue);
        } catch (ExpiredJwtException e) {
            throw new CustomException(ErrorCode.EXPIRED_REFRESH_TOKEN);
        } catch (JwtException | IllegalArgumentException e) {
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }
    }

    private void saveOrUpdateRefreshToken(Long userId, String token) {
        refreshTokenRepository.findByUserId(userId)
                .ifPresentOrElse(
                        existing -> existing.rotateToken(token),
                        () -> refreshTokenRepository.save(
                                RefreshToken.builder()
                                        .userId(userId)
                                        .token(token)
                                        .build()
                        )
                );
    }

    // ── 로그인 결과를 컨트롤러에 전달하기 위한 내부 클래스 ──

    public record LoginResult(
            LoginResponse loginResponse,
            String refreshToken,
            boolean isNewUser
    ) {}

    public record ReissueResult(
            TokenReissueResponse reissueResponse,
            String newRefreshToken,
            boolean rotated
    ) {}
}
