package unis.project.delta.domain.accountbook.dto.response;

public record BudgetSummaryResponse(
        String targetMonth,
        Long totalExpenseBudget,
        Long totalSpent,
        Long remainingBudget,
        Double usageRate
) {
    public static BudgetSummaryResponse of(String targetMonth, Long totalExpenseBudget, Long totalSpent) {
        long remaining = totalExpenseBudget - totalSpent;
        double rate = totalExpenseBudget == 0
                ? 0.0
                : Math.round((double) totalSpent / totalExpenseBudget * 1000.0) / 10.0;
        return new BudgetSummaryResponse(targetMonth, totalExpenseBudget, totalSpent, remaining, rate);
    }
}
