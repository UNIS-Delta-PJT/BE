package unis.project.delta.domain.accountbook.dto.response;

import unis.project.delta.domain.accountbook.entity.IncomeDetail;

public record IncomeDetailItemResponse(
        Long incomeDetailId,
        String category,
        Long amount
) {
    public static IncomeDetailItemResponse from(IncomeDetail incomeDetail) {
        return new IncomeDetailItemResponse(
                incomeDetail.getId(),
                incomeDetail.getCategory(),
                incomeDetail.getAmount()
        );
    }
}
