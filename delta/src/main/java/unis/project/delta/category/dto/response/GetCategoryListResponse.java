package unis.project.delta.category.dto.response;

import lombok.Builder;
import unis.project.delta.category.domain.Category;
import java.util.List;

@Builder
public record GetCategoryListResponse(
        List<CategoryInfo> categories
) {

    @Builder
    public record CategoryInfo(
            Long categoryId,
            String name,
            boolean isDefault
    ) {
        public static CategoryInfo from(Category category) {
            return CategoryInfo.builder()
                    .categoryId(category.getCategoryId())
                    .name(category.getName())
                    .isDefault(category.isDefault())
                    .build();
        }
    }

    public static GetCategoryListResponse from(List<Category> categoryList) {
        List<CategoryInfo> infoList = categoryList.stream()
                .map(CategoryInfo::from)
                .toList();

        return GetCategoryListResponse.builder()
                .categories(infoList)
                .build();
    }
}