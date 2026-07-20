package unis.project.delta.domain.quiz.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import unis.project.delta.domain.quiz.entity.DailyQuiz;
import unis.project.delta.domain.quiz.entity.UserDailyQuizHistory;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record DailyQuizResponse(
        Long quizId,
        String question,
        boolean isSubmitted,
        Boolean isCorrect,
        String correctAnswer,
        String explanation
) {
    /** 아직 풀지 않은 경우 */
    public static DailyQuizResponse notSubmitted(DailyQuiz quiz) {
        return new DailyQuizResponse(
                quiz.getId(), quiz.getQuestion(), false,
                null, null, null
        );
    }

    /** 이미 제출한 경우 */
    public static DailyQuizResponse submitted(DailyQuiz quiz, UserDailyQuizHistory history) {
        return new DailyQuizResponse(
                quiz.getId(), quiz.getQuestion(), true,
                history.getIsCorrect(), quiz.getCorrectAnswer(), quiz.getExplanation()
        );
    }
}
