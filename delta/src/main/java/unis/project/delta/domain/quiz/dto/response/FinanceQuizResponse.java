package unis.project.delta.domain.quiz.dto.response;

import unis.project.delta.domain.quiz.entity.FinanceQuiz;
import unis.project.delta.domain.quiz.entity.FinanceQuizOption;

import java.util.List;

public record FinanceQuizResponse(
        Long quizId,
        String question,
        List<OptionResponse> options
) {
    public record OptionResponse(Integer optionNumber, String content) {}

    public static FinanceQuizResponse from(FinanceQuiz quiz) {
        List<OptionResponse> opts = quiz.getOptions().stream()
                .map(o -> new OptionResponse(o.getOptionNumber(), o.getContent()))
                .toList();
        return new FinanceQuizResponse(quiz.getId(), quiz.getQuestion(), opts);
    }
}
