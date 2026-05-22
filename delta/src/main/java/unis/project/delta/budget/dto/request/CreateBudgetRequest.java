package unis.project.delta.budget.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.util.List;

public record CreateBudgetRequest(

        @NotNull(message = "예산 적용 월은 필수 입력 값입니다.")
        @Pattern(regexp = "^\\d{4}-(0[1-9]|1[0-2])$", message = "연월 형식은 YYYY-MM 이어야 합니다.")
        String yearMonth,

        @NotNull(message = "등록할 총 예산 금액은 필수 입력 값입니다.")
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