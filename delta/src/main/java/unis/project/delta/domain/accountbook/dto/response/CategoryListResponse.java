package unis.project.delta.domain.accountbook.dto.response;

import unis.project.delta.domain.accountbook.entity.ExpenseCategory;

import java.util.List;

public record CategoryListResponse(
        List<CategoryResponse> categories
) {
    public static CategoryListResponse from(List<ExpenseCategory> categories) {
        return new CategoryListResponse(
                categories.stream().map(CategoryResponse::from).toList()
        );
    }
}
