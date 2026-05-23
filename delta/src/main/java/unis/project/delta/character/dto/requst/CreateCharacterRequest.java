package unis.project.delta.character.dto.requst;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import unis.project.delta.user.domain.User;
import unis.project.delta.character.domain.Character;

@Getter
@NoArgsConstructor
public class CreateCharacterRequest {

    @NotBlank(message = "캐릭터 이름을 설정해주셔야 합니다.")
    private String name;

    public Character toEntity(User user) {
        return Character.builder()
                .name(this.name)
                .user(user)
                .build();
    }
}
