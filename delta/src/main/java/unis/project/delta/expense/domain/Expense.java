package unis.project.delta.expense.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import unis.project.delta.category.domain.Category;
import unis.project.delta.global.domain.BaseEntity;
import unis.project.delta.user.domain.User;
import java.time.LocalDate;

@Entity
@Getter
@Table(name = "expenses")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Expense extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "expense_id")
    private Long expenseId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(nullable = false)
    private Long amount;

    @Column(nullable = false)
    private LocalDate expenseDate;

    @Column(nullable = true)
    private String memo;

    @Builder
    public Expense(User user, Category category, Long amount, LocalDate expenseDate, String memo) {
        this.user = user;
        this.category = category;
        this.amount = amount;
        this.expenseDate = expenseDate;
        this.memo = memo;
    }
}