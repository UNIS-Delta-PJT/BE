package unis.project.delta.category.dto.response;

import lombok.Builder;
import unis.project.delta.category.domain.Category;

@Builder
public record CreateCategoryResponse(
    Long categoryId,
    String name,
    boolean isDefault
) {
    public static CreateCategoryResponse from(Category savedCategory) {
        return CreateCategoryResponse.builder()
                .categoryId(savedCategory.getCategoryId())
                .name(savedCategory.getName())
                .isDefault(savedCategory.isDefault())
                .build();
    }
}
