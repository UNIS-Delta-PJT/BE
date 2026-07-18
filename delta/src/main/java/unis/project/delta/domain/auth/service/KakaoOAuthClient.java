package unis.project.delta.domain.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import unis.project.delta.global.exception.CustomException;
import unis.project.delta.global.exception.ErrorCode;

import java.util.Map;

/**
 * 카카오 OAuth API 호출 클라이언트.
 * 카카오 액세스 토큰으로 사용자 정보를 조회한다.
 */
@Component
@RequiredArgsConstructor
public class KakaoOAuthClient {

    private static final String KAKAO_USER_INFO_URL = "https://kapi.kakao.com/v2/user/me";

    private final RestTemplate restTemplate;

    /**
     * 카카오 액세스 토큰으로 사용자 고유 ID(oauthId)를 조회한다.
     *
     * @param kakaoAccessToken 카카오 액세스 토큰
     * @return 카카오 사용자 고유 ID (String)
     */
    public String getKakaoUserId(String kakaoAccessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(kakaoAccessToken);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    KAKAO_USER_INFO_URL,
                    HttpMethod.GET,
                    request,
                    Map.class
            );

            Map<String, Object> body = response.getBody();
            if (body == null || !body.containsKey("id")) {
                throw new CustomException(ErrorCode.KAKAO_AUTHENTICATION_FAILED);
            }

            return String.valueOf(body.get("id"));
        } catch (HttpClientErrorException e) {
            throw new CustomException(ErrorCode.KAKAO_AUTHENTICATION_FAILED);
        }
    }
}
