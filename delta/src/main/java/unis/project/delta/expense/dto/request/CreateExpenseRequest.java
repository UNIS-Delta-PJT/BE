package unis.project.delta.expense.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CreateExpenseRequest(
        @NotNull(message = "카테고리 ID는 필수 입력 값입니다.")
        Long categoryId,

        @NotNull(message = "소비 금액은 필수 입력 값입니다.")
        @Min(value = 1, message = "소비 금액은 0원보다 커야 합니다.")
        Long amount,

        @NotNull(message = "소비 날짜는 필수 입력 값입니다.")
        String expenseDate,

        String memo
) {}