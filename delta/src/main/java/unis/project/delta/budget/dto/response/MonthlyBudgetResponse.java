package unis.project.delta.budget.dto.response;

import lombok.Builder;
import unis.project.delta.budget.domain.CategoryBudget;
import unis.project.delta.budget.domain.MonthlyBudget;
import java.util.List;

@Builder
public record MonthlyBudgetResponse(
        Long monthlyBudgetId,
        String yearMonth,
        Long totalAmount,
        Long categoryBudgetTotal,
        List<CategoryBudgetInfo> categoryBudgets
) {
    @Builder
    public record CategoryBudgetInfo(
            Long categoryBudgetId,
            Long categoryId,
            String categoryName,
            Long amount
    ) {
        public static CategoryBudgetInfo from(CategoryBudget cb) {
            return CategoryBudgetInfo.builder()
                    .categoryBudgetId(cb.getCategoryBudgetId())
                    .categoryId(cb.getCategory().getCategoryId())
                    .categoryName(cb.getCategory().getName())
                    .amount(cb.getAmount())
                    .build();
        }
    }

    public static MonthlyBudgetResponse from(MonthlyBudget savedBudget) {
        Long total = savedBudget.getCategoryBudgets().stream()
                .mapToLong(CategoryBudget::getAmount)
                .sum();

        List<CategoryBudgetInfo> infoList = savedBudget.getCategoryBudgets().stream()
                .map(CategoryBudgetInfo::from)
                .toList();

        return MonthlyBudgetResponse.builder()
                .monthlyBudgetId(savedBudget.getMonthlyBudgetId())
                .yearMonth(savedBudget.getYearMonth())
                .totalAmount(savedBudget.getTotalAmount())
                .categoryBudgetTotal(total)
                .categoryBudgets(infoList)
                .build();
    }
}