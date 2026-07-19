package unis.project.delta.domain.auth.dto.response;

public record TokenReissueResponse(
        String accessToken,
        String tokenType,
        long expiresIn,
        boolean refreshTokenRotated
) {
    public static TokenReissueResponse from(String accessToken, long expiresIn, boolean refreshTokenRotated) {
        return new TokenReissueResponse(accessToken, "Bearer", expiresIn, refreshTokenRotated);
    }
}
