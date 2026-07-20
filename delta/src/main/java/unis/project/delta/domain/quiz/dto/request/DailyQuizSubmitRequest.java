package unis.project.delta.domain.quiz.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DailyQuizSubmitRequest {

    @NotNull(message = "퀴즈 ID는 필수입니다.")
    private Long quizId;

    @NotNull(message = "답은 필수입니다.")
    @Pattern(regexp = "^[OX]$", message = "답은 O 또는 X만 입력할 수 있습니다.")
    private String answer;
}
