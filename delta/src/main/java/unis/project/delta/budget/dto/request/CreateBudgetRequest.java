package unis.project.delta.budget.dto.request;

import java.util.List;

public record CreateBudgetRequest(
        String yearMonth,
        Long totalAmount,
        List<CategoryBudgetDto> categoryBudgets
) {
    public record CategoryBudgetDto(
            Long categoryId,
            Long amount
    ) {}
}