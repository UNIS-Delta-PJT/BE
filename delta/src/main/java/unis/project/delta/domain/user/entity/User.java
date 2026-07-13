package unis.project.delta.domain.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 카카오 고유 식별자
    @Column(nullable = false, unique = true)
    private String oauthId;

    // 사용자가 설정한 캐릭터 닉네임
    @Column(nullable = false)
    private String nickname;

    // 선택한 캐릭터 몸통 색상
    @Column(nullable = false)
    private String bodyColor;

    // 선택한 캐릭터 눈 모양
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EyeShape eyeShape;

    // 현재 보유 중인 코인 잔액
    @Column(nullable = false)
    private Integer coinBalance;

    // 연속 출석 일수
    @Column(nullable = false)
    private Integer continuousAttendance;

    // 현재 위치하고 있는 맵 칸수
    @Column(nullable = false)
    private Integer mapPosition;

    @Builder
    public User(String oauthId, String nickname, String bodyColor, EyeShape eyeShape) {
        this.oauthId = oauthId;
        this.nickname = nickname;
        this.bodyColor = bodyColor;
        this.eyeShape = eyeShape;
        this.coinBalance = 0;
        this.continuousAttendance = 0;
        this.mapPosition = 1;
    }

    public void updateBodyColor(String newBodyColor) {
        this.bodyColor = newBodyColor;
    }

    public void updateEyeShape(EyeShape newEyeShape) {
        this.eyeShape = newEyeShape;
    }

    public void increaseCoinBalance(Integer coinCnt) {
        this.coinBalance += coinCnt;
    }

    public void decreaseCoinBalance(Integer coinCnt) {
        if (this.coinBalance - coinCnt >= 0) {
            this.coinBalance -= coinCnt;
        }
    }

    public void increaseContinuousAttendance() {
        this.continuousAttendance++;
    }

    public void resetContinuousAttendance() {
        this.continuousAttendance = 0;
    }

    public void increaseMapPosition(Integer cnt) {
        this.mapPosition += cnt;
    }

    public void decreaseMapPosition(Integer cnt) {
        if (this.mapPosition - cnt > 0) {
            this.mapPosition -= cnt;
        }
    }

    public void resetMapPosition() {
        this.mapPosition = 1;
    }
}
