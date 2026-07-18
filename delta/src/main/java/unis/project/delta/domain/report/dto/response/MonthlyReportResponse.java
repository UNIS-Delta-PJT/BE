package unis.project.delta.domain.report.dto.response;

import java.util.List;

public record MonthlyReportResponse(
        String targetMonth,
        Long totalExpenseBudget,
        Long totalSpent,
        Long remainingBudget,
        Double usageRate,
        List<RankedCategoryDto> topCategories,
        List<TopExpenseDto> topExpenses,
        LastMonthComparisonDto lastMonthComparison
) {
    public record RankedCategoryDto(
            Integer rank,
            Long categoryId,
            String categoryName,
            Long amount,
            Double percentage
    ) {}

    public record TopExpenseDto(
            Long expenseId,
            String placeName,
            Long amount,
            String categoryName,
            String expenseDate
    ) {}

    public record LastMonthComparisonDto(
            Long lastMonthTotalSpent,
            Long changeAmount
    ) {}

    public static MonthlyReportResponse of(
            String targetMonth, Long totalExpenseBudget, Long totalSpent,
            List<RankedCategoryDto> topCategories, List<TopExpenseDto> topExpenses,
            LastMonthComparisonDto comparison) {
        long remaining = totalExpenseBudget - totalSpent;
        double rate = totalExpenseBudget == 0
                ? 0.0
                : Math.round((double) totalSpent / totalExpenseBudget * 1000.0) / 10.0;
        return new MonthlyReportResponse(
                targetMonth, totalExpenseBudget, totalSpent, remaining, rate,
                topCategories, topExpenses, comparison);
    }
}
