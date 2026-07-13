package unis.project.delta.domain.accountbook.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import unis.project.delta.domain.user.entity.User;
import unis.project.delta.global.exception.CustomException;
import unis.project.delta.global.exception.ErrorCode;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "expense_record")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExpenseRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private ExpenseCategory expenseCategory;

    // 실제 지출 금액
    @Column(nullable = false)
    private Long amount;

    // 사용처
    @Column(nullable = false)
    private String placeName;

    // 지출 날짜 및 시간
    @Column(nullable = false)
    private LocalDateTime expenseDate;

    // 메모(선택사항)
    @Column
    private String memo;

    @Builder
    public ExpenseRecord(User user, ExpenseCategory expenseCategory, Long amount, String placeName, LocalDateTime expenseDate, String memo) {
        this.user = user;
        this.expenseCategory = expenseCategory;
        this.amount = amount;
        this.placeName = placeName;
        this.expenseDate = expenseDate;
        this.memo = memo;
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

    public void updatePlaceName(String newPlaceName) {
        this.placeName = newPlaceName;
    }

    public void updateExpenseDate(LocalDateTime newExpenseDate) {
        this.expenseDate = newExpenseDate;
    }

    public void updateMemo(String newMemo) {
        this.memo = newMemo;
    }
}
