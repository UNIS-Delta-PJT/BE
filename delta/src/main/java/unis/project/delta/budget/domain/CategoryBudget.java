package unis.project.delta.budget.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import unis.project.delta.category.domain.Category;
import unis.project.delta.global.domain.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CategoryBudget extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_budget_id")
    private Long categoryBudgetId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "monthly_budget_id")
    private MonthlyBudget monthlyBudget;

    @Column(nullable = false)
    private Long amount;

    @Builder
    public CategoryBudget(Category category, Long amount) {
        this.amount=amount;
        this.category=category;
    }

    // 부모 객체가 나를 추가할 때 호출해 줄 Setter
    public void setMonthlyBudget(MonthlyBudget monthlyBudget) {
        this.monthlyBudget = monthlyBudget;
    }

    public void updateAmount(Long amount) {
        this.amount = amount;
    }
}
