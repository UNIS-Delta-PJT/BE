package unis.project.delta.domain.user.dto.response;

import unis.project.delta.domain.user.entity.User;

public record CharacterResponse(
        String nickname,
        String bodyColor,
        String eyeShape
) {
    public static CharacterResponse from(User user) {
        return new CharacterResponse(
                user.getNickname(),
                user.getBodyColor().name(),
                user.getEyeShape().name()
        );
    }
}
