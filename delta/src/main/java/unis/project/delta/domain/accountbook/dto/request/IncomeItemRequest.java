package unis.project.delta.domain.accountbook.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class IncomeItemRequest {

    @NotBlank(message = "수입 카테고리는 필수입니다.")
    private String category;

    @NotNull(message = "수입 금액은 필수입니다.")
    @PositiveOrZero(message = "금액은 0 이상이어야 합니다.")
    private Long amount;
}
