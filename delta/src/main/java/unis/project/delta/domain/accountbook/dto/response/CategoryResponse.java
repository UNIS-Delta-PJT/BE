package unis.project.delta.domain.accountbook.dto.response;

import unis.project.delta.domain.accountbook.entity.ExpenseCategory;

public record CategoryResponse(
        Long categoryId,
        String name,
        boolean isDefault
) {
    public static CategoryResponse from(ExpenseCategory category) {
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getIsDefault()
        );
    }
}
