package unis.project.delta.character.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import unis.project.delta.user.domain.User;

@Entity
@Getter
@Table(name = "characters")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Character {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "character_id")
    private Long characterId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int feelingXp;

    @Column(nullable = false)
    private int coin;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public Character(String name, User user) {
        this.name = name;
        this.feelingXp = 0;
        this.coin = 0;
        this.user = user;
    }

    // 유저 세팅
    public void assignUser(User user) {
        this.user = user;
    }
}
