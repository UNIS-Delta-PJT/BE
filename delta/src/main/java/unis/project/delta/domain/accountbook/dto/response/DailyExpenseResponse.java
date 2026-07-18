package unis.project.delta.domain.accountbook.dto.response;

import unis.project.delta.domain.accountbook.entity.ExpenseRecord;

import java.time.LocalDate;
import java.util.List;

public record DailyExpenseResponse(
        String date,
        Long dailyTotalExpense,
        List<ExpenseDetailResponse> expenses
) {
    public static DailyExpenseResponse of(LocalDate date, Long dailyTotalExpense,
                                          List<ExpenseRecord> records) {
        List<ExpenseDetailResponse> expenses = records.stream()
                .map(ExpenseDetailResponse::from)
                .toList();
        return new DailyExpenseResponse(date.toString(), dailyTotalExpense, expenses);
    }
}
