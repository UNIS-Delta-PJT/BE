package unis.project.delta.domain.quiz.dto.response;

public record FinanceQuizSubmitResponse(
        boolean isCorrect,
        Integer correctOption,
        String explanation,
        Integer rewardCoin,
        Integer coinBalance,
        boolean isDiceEnabled
) {
    public static FinanceQuizSubmitResponse of(boolean isCorrect, Integer correctOption,
                                               String explanation, Integer rewardCoin,
                                               Integer coinBalance, boolean isDiceEnabled) {
        return new FinanceQuizSubmitResponse(isCorrect, correctOption, explanation,
                rewardCoin, coinBalance, isDiceEnabled);
    }
}
