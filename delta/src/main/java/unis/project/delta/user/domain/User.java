package unis.project.delta.user.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import unis.project.delta.character.domain.Character;
import unis.project.delta.global.domain.BaseEntity;

@Entity
@Getter
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(nullable = false, unique = true, length = 36)
    private String uuid;

    @Column(nullable = true)
    private String nickname;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Character character;

    @Builder
    public User(String uuid, String nickname) {
        this.uuid = uuid;
        this.nickname = nickname;
    }

    // 유저를 만들 때 캐릭터도 서로 안전하게 매핑되도록 하는 메서드
    public void assignCharacter(Character character) {
        this.character = character;
        if(character.getUser() != this) {
            character.assignUser(this);
        }
    }
}
