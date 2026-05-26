package unis.project.delta.expense.dto.response;

import lombok.Builder;
import unis.project.delta.expense.domain.Expense;

@Builder
public record CreateExpenseResponse(
        Long expenseId,
        Long categoryId,
        String categoryName,
        Long amount,
        String expenseDate,
        String memo
) {
    public static CreateExpenseResponse from(Expense expense) {
        return CreateExpenseResponse.builder()
                .expenseId(expense.getExpenseId())
                .categoryId(expense.getCategory().getCategoryId())
                .categoryName(expense.getCategory().getName())
                .amount(expense.getAmount())
                .expenseDate(expense.getExpenseDate().toString())
                .memo(expense.getMemo())
                .build();
    }
}