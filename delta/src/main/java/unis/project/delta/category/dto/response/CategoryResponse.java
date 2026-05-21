package unis.project.delta.category.dto.response;

import lombok.Builder;
import unis.project.delta.category.domain.Category;

@Builder
public record CategoryResponse (
    Long categoryId,
    String name,
    boolean isDefault
) {
    public static CategoryResponse from(Category savedCategory) {
        return CategoryResponse.builder()
                .categoryId(savedCategory.getCategoryId())
                .name(savedCategory.getName())
                .isDefault(savedCategory.isDefault())
                .build();
    }
}
