package unis.project.delta.user.dto.response;

import lombok.Builder;
import unis.project.delta.user.domain.User;

@Builder
public record UserResponse (
        Long userId,
        String uuid,
        String nickname,
        Long characterId
) {
    public static UserResponse from(User savedUser) {
        return UserResponse.builder()
                .userId(savedUser.getUserId())
                .uuid(savedUser.getUuid())
                .nickname(savedUser.getNickname())
                .characterId(savedUser.getCharacter().getCharacterId())
                .build();
    }
}
