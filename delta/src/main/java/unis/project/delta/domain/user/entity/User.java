package unis.project.delta.domain.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import unis.project.delta.global.exception.CustomException;
import unis.project.delta.global.exception.ErrorCode;

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
    @Column
    private String nickname;

    // 선택한 캐릭터 몸통 색상
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BodyColor bodyColor;

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

    // 전체 푸시 알림 허용 여부
    @Column(nullable = false)
    private Boolean isPushEnabled;

    // 야간 푸시 알림 제한 여부
    @Column(nullable = false)
    private Boolean isNightPushDisabled;

    // FCM 토큰
    @Column
    private String fcmToken;

    @Builder
    public User(String oauthId, String nickname, String fcmToken) {
        this.oauthId = oauthId;
        this.nickname = nickname;
        this.bodyColor = BodyColor.WHITE;
        this.eyeShape = EyeShape.DEFAULT;
        this.coinBalance = 0;
        this.continuousAttendance = 0;
        this.mapPosition = 1;
        this.isPushEnabled = true;
        this.isNightPushDisabled = false;
        this.fcmToken = fcmToken;
    }

    public void updateBodyColor(BodyColor newBodyColor) {
        this.bodyColor = newBodyColor;
    }

    public void updateEyeShape(EyeShape newEyeShape) {
        this.eyeShape = newEyeShape;
    }

    public void increaseCoinBalance(Integer coinCnt) {
        this.coinBalance += coinCnt;
    }

    public void decreaseCoinBalance(Integer coinCnt) {
        if (this.coinBalance < coinCnt) {
            throw new CustomException(ErrorCode.INSUFFICIENT_COIN);
        }
        this.coinBalance -= coinCnt;
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
        if (this.mapPosition - cnt < 1) {
            throw new CustomException(ErrorCode.INVALID_MAP_POSITION);
        }
        this.mapPosition -= cnt;
    }

    public void resetMapPosition() {
        this.mapPosition = 1;
    }

    public void switchPush(Boolean newPush) {
        this.isPushEnabled = newPush;
    }

    public void switchNightPush(Boolean newPush) {
        this.isNightPushDisabled = newPush;
    }

    public void updateFcmToken(String newFcmToken) {
        this.fcmToken = newFcmToken;
    }
}
