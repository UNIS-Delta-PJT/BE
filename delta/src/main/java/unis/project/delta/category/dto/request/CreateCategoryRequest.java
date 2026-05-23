package unis.project.delta.category.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import unis.project.delta.category.domain.Category;
import unis.project.delta.user.domain.User;

@Getter
@NoArgsConstructor
public class CreateCategoryRequest {

    @NotBlank(message = "카테고리 이름을 입력해 주세요.")
    private String name;

    public Category toEntity(User user) {
        return Category.builder()
                .name(this.name)
                .user(user)
                .isDefault(false)
                .build();
    }
}
