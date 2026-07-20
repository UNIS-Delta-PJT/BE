package unis.project.delta.domain.accountbook.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExpenseItemRequest {

    @NotNull(message = "지출 금액은 필수입니다.")
    @Positive(message = "지출 금액은 양수여야 합니다.")
    private Long amount;

    @NotBlank(message = "사용처는 필수입니다.")
    private String placeName;

    @NotNull(message = "카테고리 ID는 필수입니다.")
    private Long categoryId;

    @NotBlank(message = "지출 날짜는 필수입니다.")
    private String expenseDate;

    private String memo;
}
