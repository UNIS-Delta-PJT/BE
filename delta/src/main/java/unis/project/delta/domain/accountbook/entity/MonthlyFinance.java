package unis.project.delta.domain.accountbook.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import unis.project.delta.domain.user.entity.User;
import unis.project.delta.global.exception.CustomException;
import unis.project.delta.global.exception.ErrorCode;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "monthly_finance")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MonthlyFinance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 대상 월 (예: "2026-07")
    @Column(nullable = false)
    private String targetMonth;

    // 한 달 총 수입
    @Column(nullable = false)
    private Long totalIncome;

    // 한 달 목표 지출 예산 총액
    @Column(nullable = false)
    private Long totalExpenseBudget;

    // 저축 목표 금액
    @Column(nullable = false)
    private Long targetSavings;

    // 저축 유형
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SavingsType savingsType;

    // 양방향 매핑: 이달의 지출 예산 리스트
    @OneToMany(mappedBy = "monthlyFinance", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ExpenseBudget> expenseBudgets = new ArrayList<>();

    @Builder
    public MonthlyFinance(User user, String targetMonth, Long totalIncome, Long totalExpenseBudget, Long targetSavings, SavingsType savingsType) {
        this.user = user;
        this.targetMonth = targetMonth;
        this.totalIncome = totalIncome;
        this.totalExpenseBudget = totalExpenseBudget;
        this.targetSavings = targetSavings;
        this.savingsType = savingsType;
    }

    public void updateTargetMonth() {
        YearMonth current = YearMonth.parse(this.targetMonth);
        YearMonth nextMonth = current.plusMonths(1);
        this.targetMonth = nextMonth.toString();
    }

    public void updateIncome(Long newTotalIncome) {
        if(newTotalIncome < 0) {
            throw new CustomException(ErrorCode.INVALID_AMOUNT);
        }
        this.totalIncome = newTotalIncome;
    }

    public void updateExpenseBudget(Long amount) {
        if (amount < 0) {
            throw new CustomException(ErrorCode.INVALID_AMOUNT);
        }
        this.totalExpenseBudget = amount;
    }

    public void updateTargetSavings(Long newTargetSavings) {
        if (newTargetSavings < 0) {
            throw new CustomException(ErrorCode.INVALID_AMOUNT);
        }
        this.targetSavings = newTargetSavings;
    }

    public void updateSavingsType(SavingsType newSavingsType) {
        this.savingsType = newSavingsType;
    }
}
