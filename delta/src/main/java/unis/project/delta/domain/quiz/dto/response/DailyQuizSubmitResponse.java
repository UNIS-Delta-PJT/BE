package unis.project.delta.domain.quiz.dto.response;

public record DailyQuizSubmitResponse(
        boolean isCorrect,
        String correctAnswer,
        String explanation,
        Integer rewardCoin,
        Integer coinBalance
) {
    public static DailyQuizSubmitResponse of(boolean isCorrect, String correctAnswer,
                                             String explanation, Integer rewardCoin,
                                             Integer coinBalance) {
        return new DailyQuizSubmitResponse(isCorrect, correctAnswer, explanation,
                rewardCoin, coinBalance);
    }
}
