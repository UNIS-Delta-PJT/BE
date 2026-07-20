package unis.project.delta.domain.accountbook.dto.response;

import unis.project.delta.domain.accountbook.entity.ExpenseRecord;

public record ExpenseDetailResponse(
        Long expenseId,
        Long amount,
        String placeName,
        Long categoryId,
        String categoryName,
        String expenseDate,
        String memo
) {
    public static ExpenseDetailResponse from(ExpenseRecord record) {
        return new ExpenseDetailResponse(
                record.getId(),
                record.getAmount(),
                record.getPlaceName(),
                record.getExpenseCategory().getId(),
                record.getExpenseCategory().getName(),
                record.getExpenseDate().toString(),
                record.getMemo()
        );
    }
}
