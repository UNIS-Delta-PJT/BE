package unis.project.delta.domain.accountbook.dto.response;

import unis.project.delta.domain.accountbook.entity.ExpenseBudget;

public record ExpenseBudgetItemResponse(
        Long expenseBudgetId,
        Long categoryId,
        String categoryName,
        Long amount
) {
    public static ExpenseBudgetItemResponse from(ExpenseBudget budget) {
        return new ExpenseBudgetItemResponse(
                budget.getId(),
                budget.getExpenseCategory().getId(),
                budget.getExpenseCategory().getName(),
                budget.getAmount()
        );
    }
}
