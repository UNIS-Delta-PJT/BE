package unis.project.delta.budget.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record UpdateBudgetRequest(
        @NotNull(message = "수정할 총 예산 금액은 필수 입력 값입니다.")
        @Min(value = 0, message = "예산 금액은 0원 이상이어야 합니다.")
        Long totalAmount,

        @NotNull(message = "카테고리별 예산 목록은 필수 입력 값입니다.")
        @Valid
        List<CategoryBudgetDto> categoryBudgets
) {
    public record CategoryBudgetDto(
            @NotNull(message = "카테고리 ID는 필수 입력 값입니다.")
            Long categoryId,

            @NotNull(message = "카테고리별 예산 금액은 필수 입력 값입니다.")
            @Min(value = 0, message = "카테고리 예산 금액은 0원 이상이어야 합니다.")
            Long amount
    ) {}
}