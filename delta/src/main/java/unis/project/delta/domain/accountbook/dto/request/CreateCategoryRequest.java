package unis.project.delta.domain.accountbook.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateCategoryRequest {

    @NotBlank(message = "카테고리명은 필수입니다.")
    private String name;
}
