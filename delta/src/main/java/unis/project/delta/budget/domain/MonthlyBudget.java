package unis.project.delta.budget.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import unis.project.delta.global.domain.BaseEntity;
import unis.project.delta.user.domain.User;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MonthlyBudget extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "monthly_budget_id")
    private Long monthlyBudgetId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // year_month가 DB 예약어라고 합니당..
    @Column(nullable = false, name = "budget_year_month")
    private String yearMonth;

    @Column(nullable = false)
    private Long totalAmount;

    @OneToMany(mappedBy = "monthlyBudget", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CategoryBudget> categoryBudgets = new ArrayList<>();

    @Builder
    public MonthlyBudget(User user, String yearMonth, Long totalAmount) {
        this.user = user;
        this.yearMonth = yearMonth;
        this.totalAmount = totalAmount;
    }

    public void addCategoryBudget(CategoryBudget categoryBudget) {
        this.categoryBudgets.add(categoryBudget);
        categoryBudget.setMonthlyBudget(this); // 자식에게 부모 주입
    }
}
