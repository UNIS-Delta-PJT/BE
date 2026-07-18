package unis.project.delta.global.config.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secretKeyPlain;

    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpiration;    // 밀리초 (예: 3600000 = 1시간)

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;   // 밀리초 (예: 1209600000 = 14일)

    private SecretKey secretKey;

    @PostConstruct
    protected void init() {
        byte[] keyBytes = Base64.getDecoder().decode(secretKeyPlain);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    // ── Access Token 생성 ──
    public String createAccessToken(Long userId) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + accessTokenExpiration);

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .issuedAt(now)
                .expiration(expiry)
                .signWith(secretKey)
                .compact();
    }

    // ── Refresh Token 생성 ──
    public String createRefreshToken(Long userId) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + refreshTokenExpiration);

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .issuedAt(now)
                .expiration(expiry)
                .signWith(secretKey)
                .compact();
    }

    // ── 토큰에서 userId 추출 ──
    public Long getUserIdFromToken(String token) {
        Claims claims = parseClaims(token);
        return Long.parseLong(claims.getSubject());
    }

    // ── Access Token 유효성 검증 ──
    public boolean validateAccessToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // ── Refresh Token 유효성 검증 (만료 여부 구분) ──
    public void validateRefreshToken(String token) {
        try {
            parseClaims(token);
        } catch (ExpiredJwtException e) {
            throw e;  // 서비스 계층에서 EXPIRED_REFRESH_TOKEN으로 처리
        } catch (JwtException | IllegalArgumentException e) {
            throw e;  // 서비스 계층에서 INVALID_REFRESH_TOKEN으로 처리
        }
    }

    // ── Access Token 만료 시간(초) 반환 ──
    public long getAccessTokenExpirationInSeconds() {
        return accessTokenExpiration / 1000;
    }

    // ── Refresh Token 로테이션 필요 여부 판단 ──
    // 남은 유효기간이 전체의 50% 미만이면 로테이션
    public boolean shouldRotateRefreshToken(String token) {
        Claims claims = parseClaims(token);
        Date expiration = claims.getExpiration();
        long remaining = expiration.getTime() - System.currentTimeMillis();
        return remaining < (refreshTokenExpiration / 2);
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
