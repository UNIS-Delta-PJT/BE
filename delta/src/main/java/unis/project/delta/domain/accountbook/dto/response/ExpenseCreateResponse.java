package unis.project.delta.domain.accountbook.dto.response;

public record ExpenseCreateResponse(
        int savedCount,
        boolean isFirstRecordOfDay,
        Long dailyTotalExpense
) {
    public static ExpenseCreateResponse of(int savedCount, boolean isFirstRecordOfDay, Long dailyTotalExpense) {
        return new ExpenseCreateResponse(savedCount, isFirstRecordOfDay, dailyTotalExpense);
    }
}
