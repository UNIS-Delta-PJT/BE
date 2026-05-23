package unis.project.delta.expense.dto.response;

import lombok.Builder;
import unis.project.delta.expense.domain.Expense;
import java.util.List;

@Builder
public record GetExpenseListResponse(
        String yearMonth,
        List<ExpenseDetail> expenses
) {
    @Builder
    public record ExpenseDetail(
            Long expenseId,
            Long categoryId,
            String categoryName,
            Long amount,
            String expenseDate,
            String memo
    ) {
        public static ExpenseDetail from(Expense expense) {
            return ExpenseDetail.builder()
                    .expenseId(expense.getExpenseId())
                    .categoryId(expense.getCategory().getCategoryId())
                    .categoryName(expense.getCategory().getName())
                    .amount(expense.getAmount())
                    .expenseDate(expense.getExpenseDate().toString())
                    .memo(expense.getMemo())
                    .build();
        }
    }

    public static GetExpenseListResponse of(String yearMonth, List<Expense> expenses) {
        List<ExpenseDetail> details = expenses.stream()
                .map(ExpenseDetail::from)
                .toList();

        return GetExpenseListResponse.builder()
                .yearMonth(yearMonth)
                .expenses(details)
                .build();
    }
}