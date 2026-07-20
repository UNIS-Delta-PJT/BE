package unis.project.delta.domain.accountbook.dto.response;

public record UpdateIncomeResponse(
        Long totalIncome
) {
    public static UpdateIncomeResponse from(Long totalIncome) {
        return new UpdateIncomeResponse(totalIncome);
    }
}
