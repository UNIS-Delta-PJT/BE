package unis.project.delta.domain.accountbook.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UpdateExpenseBudgetRequest {

    @NotNull(message = "총 지출 예산은 필수입니다.")
    @PositiveOrZero(message = "금액은 0 이상이어야 합니다.")
    private Long totalExpenseBudget;

    @NotEmpty(message = "카테고리별 지출 예산은 1건 이상이어야 합니다.")
    @Valid
    private List<BudgetItemRequest> expenseBudgets;
}
