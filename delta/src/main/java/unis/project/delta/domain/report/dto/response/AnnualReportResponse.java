package unis.project.delta.domain.report.dto.response;

import java.util.List;

public record AnnualReportResponse(
        Integer year,
        List<MonthlyExpenseDto> monthlyExpenses,
        AnnualSummaryDto annualSummary,
        List<CategorySavingsDto> categorySavings
) {
    public record MonthlyExpenseDto(
            String month,
            Long totalSpent,
            Long totalBudget
    ) {}

    public record AnnualSummaryDto(
            Long totalSpent,
            Long totalBudget,
            Long totalSaved,
            String highestSpendingMonth,
            String lowestSpendingMonth
    ) {}

    public record CategorySavingsDto(
            Long categoryId,
            String categoryName,
            Long savedAmount
    ) {}

    public static AnnualReportResponse of(
            Integer year, List<MonthlyExpenseDto> monthlyExpenses,
            AnnualSummaryDto annualSummary, List<CategorySavingsDto> categorySavings) {
        return new AnnualReportResponse(year, monthlyExpenses, annualSummary, categorySavings);
    }
}
