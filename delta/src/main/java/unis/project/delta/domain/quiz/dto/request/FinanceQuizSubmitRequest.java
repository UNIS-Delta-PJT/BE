package unis.project.delta.domain.quiz.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FinanceQuizSubmitRequest {

    @NotNull(message = "퀴즈 ID는 필수입니다.")
    private Long quizId;

    @NotNull(message = "선택지 번호는 필수입니다.")
    @Min(value = 1, message = "선택지 번호는 1~4 사이여야 합니다.")
    @Max(value = 4, message = "선택지 번호는 1~4 사이여야 합니다.")
    private Integer selectedOption;
}
