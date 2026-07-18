package unis.project.delta.domain.accountbook.dto.response;

public record UpdateSavingsResponse(
        Long targetSavings,
        String savingsType
) {
    public static UpdateSavingsResponse of(Long targetSavings, String savingsType) {
        return new UpdateSavingsResponse(targetSavings, savingsType);
    }
}
