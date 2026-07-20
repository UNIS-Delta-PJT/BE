package unis.project.delta.domain.report.dto.response;

import java.util.List;

public record WeeklyReportResponse(
        String weekStartDate,
        String weekEndDate,
        List<DailyExpenseDto> dailyExpenses,
        Long weeklyTotalExpense,
        String maxExpenseDay,
        LastWeekComparisonDto lastWeekComparison,
        TopCategoryDto topCategory,
        PeerRankingDto peerRanking,
        List<CategoryExpenseDto> categoryExpenses
) {
    public record DailyExpenseDto(
            String dayOfWeek,
            String date,
            Long amount
    ) {}

    public record LastWeekComparisonDto(
            Long lastWeekTotalExpense,
            Long changeAmount,
            Double changeRate
    ) {}

    public record TopCategoryDto(
            Long categoryId,
            String categoryName,
            Long amount
    ) {}

    public record PeerRankingDto(
            Integer percentile
    ) {}

    public record CategoryExpenseDto(
            Long categoryId,
            String categoryName,
            Long amount,
            Double percentage
    ) {}

    public static WeeklyReportResponse of(
            String weekStartDate, String weekEndDate,
            List<DailyExpenseDto> dailyExpenses, Long weeklyTotal, String maxExpenseDay,
            LastWeekComparisonDto comparison, TopCategoryDto topCategory,
            PeerRankingDto peerRanking, List<CategoryExpenseDto> categoryExpenses) {
        return new WeeklyReportResponse(
                weekStartDate, weekEndDate, dailyExpenses, weeklyTotal,
                maxExpenseDay, comparison, topCategory, peerRanking, categoryExpenses);
    }
}
