package unis.project.delta.domain.group.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "groups")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 프론트엔드가 URL에 사용할 초대 링크용 코드
    @Column(nullable = false, unique = true)
    private String inviteCode;

    @Builder
    public Group(String inviteCode) {
        this.inviteCode = inviteCode;
    }
}
