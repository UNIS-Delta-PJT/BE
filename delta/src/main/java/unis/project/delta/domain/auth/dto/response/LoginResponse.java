package unis.project.delta.domain.auth.dto.response;

public record LoginResponse(
        String accessToken,
        String tokenType,
        long expiresIn,
        boolean isNewUser,
        Long userId
) {
    public static LoginResponse from(String accessToken, long expiresIn, boolean isNewUser, Long userId) {
        return new LoginResponse(accessToken, "Bearer", expiresIn, isNewUser, userId);
    }
}
