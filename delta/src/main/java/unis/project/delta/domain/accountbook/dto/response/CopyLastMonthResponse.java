package unis.project.delta.domain.accountbook.dto.response;

import unis.project.delta.domain.accountbook.entity.ExpenseBudget;

import java.util.List;

public record CopyLastMonthResponse(
        String sourceMonth,
        Long totalExpenseBudget,
        List<ExpenseBudgetItemResponse> expenseBudgets
) {
    public static CopyLastMonthResponse of(String sourceMonth, Long totalExpenseBudget,
                                           List<ExpenseBudget> budgets) {
        return new CopyLastMonthResponse(
                sourceMonth,
                totalExpenseBudget,
                budgets.stream().map(ExpenseBudgetItemResponse::from).toList()
        );
    }
}
