package unis.project.delta.domain.accountbook.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateExpensesRequest {

    @NotEmpty(message = "지출 내역은 1건 이상이어야 합니다.")
    @Valid
    private List<ExpenseItemRequest> expenses;
}
