package unis.project.delta.domain.accountbook.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import unis.project.delta.global.exception.CustomException;
import unis.project.delta.global.exception.ErrorCode;

@Entity
@Getter
@Table(name = "expense_budget")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExpenseBudget {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "monthly_finance_id", nullable = false)
    private MonthlyFinance monthlyFinance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private ExpenseCategory expenseCategory;

    // 해당 카테고리에 할당한 목표 지출 금액
    @Column(nullable = false)
    private Long amount;

    @Builder
    public ExpenseBudget(MonthlyFinance monthlyFinance, ExpenseCategory expenseCategory, Long amount) {
        this.monthlyFinance = monthlyFinance;
        this.expenseCategory = expenseCategory;
        this.amount = amount;
    }

    public void updateExpenseCategory(ExpenseCategory newCategory) {
        this.expenseCategory = newCategory;
    }

    public void updateAmount(Long newAmount) {
        if (newAmount < 0) {
            throw new CustomException(ErrorCode.INVALID_AMOUNT);
        }
        this.amount = newAmount;
    }
}
