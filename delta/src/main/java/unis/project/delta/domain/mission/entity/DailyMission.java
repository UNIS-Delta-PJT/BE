package unis.project.delta.domain.mission.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import unis.project.delta.domain.user.entity.User;

import java.time.LocalDate;

@Entity
@Getter
@Table(name = "daily_missions")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DailyMission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 미션 대상 날짜
    @Column(nullable = false)
    private LocalDate targetDate;

    // 출석체크 달성 여부
    @Column(nullable = false)
    private Boolean isAttendanceDone;

    // 출석체크 리워드 수령 여부
    @Column(nullable = false)
    private Boolean isAttendanceRewarded;

    // 오늘의 지출 1회 기록 달성 여부
    @Column(nullable = false)
    private Boolean isExpenseDone;

    // 오늘의 지출 1회 기록 리워드 수령 여부
    @Column(nullable = false)
    private Boolean isExpenseRewarded;

    // 주사위 1회 굴리기 달성 여부
    @Column(nullable = false)
    private Boolean isDiceDone;

    // 주사위 1회 굴리기 리워드 수령 여부
    @Column(nullable = false)
    private Boolean isDiceRewarded;

    @Builder
    public DailyMission(User user, LocalDate targetDate) {
        this.user = user;
        this.targetDate = targetDate;
        this.isAttendanceDone = false;
        this.isAttendanceRewarded = false;
        this.isExpenseDone = false;
        this.isExpenseRewarded = false;
        this.isDiceDone = false;
        this.isDiceRewarded = false;
    }

    // ── 출석 ──

    public void completeAttendance() {
        this.isAttendanceDone = true;
    }

    public void rewardAttendance() {
        this.isAttendanceRewarded = true;
    }

    // ── 지출 기록 ──

    public void completeExpense() {
        this.isExpenseDone = true;
    }

    public void rewardExpense() {
        this.isExpenseRewarded = true;
    }

    // ── 주사위 ──

    public void completeDice() {
        this.isDiceDone = true;
    }

    public void rewardDice() {
        this.isDiceRewarded = true;
    }

    // ── 미션 타입별 달성 여부 조회 ──

    public boolean isDone(DailyMissionType type) {
        return switch (type) {
            case ATTENDANCE -> this.isAttendanceDone;
            case EXPENSE_RECORD -> this.isExpenseDone;
            case DICE -> this.isDiceDone;
        };
    }

    // ── 미션 타입별 리워드 수령 여부 조회 ──

    public boolean isRewarded(DailyMissionType type) {
        return switch (type) {
            case ATTENDANCE -> this.isAttendanceRewarded;
            case EXPENSE_RECORD -> this.isExpenseRewarded;
            case DICE -> this.isDiceRewarded;
        };
    }

    // ── 미션 타입별 리워드 수령 처리 ──

    public void reward(DailyMissionType type) {
        switch (type) {
            case ATTENDANCE -> rewardAttendance();
            case EXPENSE_RECORD -> rewardExpense();
            case DICE -> rewardDice();
        }
    }
}
