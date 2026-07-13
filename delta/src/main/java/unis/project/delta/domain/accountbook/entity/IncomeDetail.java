package unis.project.delta.domain.accountbook.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "income_detail")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class IncomeDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "monthly_finance_id", nullable = false)
    private MonthlyFinance monthlyFinance;

    // 수입 카테고리(예. 용돈, 알바 등)
    @Column(nullable = false)
    private String category;

    // 해당 카테고리의 수입 금액
    @Column(nullable = false)
    private Long amount;

    @Builder
    public IncomeDetail(MonthlyFinance monthlyFinance, String category, Long amount) {
        this.monthlyFinance = monthlyFinance;
        this.category = category;
        this.amount = amount;
    }

    public void updateCategory(String newCategory) {
        this.category = newCategory;
    }

    public void updateAmount(Long newAmount) {
        this.amount = newAmount;
    }
}
