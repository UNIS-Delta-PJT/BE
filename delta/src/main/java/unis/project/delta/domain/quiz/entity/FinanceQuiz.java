package unis.project.delta.domain.quiz.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "finance_quizzes")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FinanceQuiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String question;

    /** 정답 선택지 번호 (1~4) */
    @Column(nullable = false)
    private Integer correctOption;

    @Column(nullable = false, length = 1000)
    private String explanation;

    @ElementCollection
    @CollectionTable(name = "finance_quiz_options",
            joinColumns = @JoinColumn(name = "finance_quiz_id"))
    @OrderBy("optionNumber ASC")
    private List<FinanceQuizOption> options = new ArrayList<>();

    @Builder
    public FinanceQuiz(String question, Integer correctOption, String explanation,
                       List<FinanceQuizOption> options) {
        this.question = question;
        this.correctOption = correctOption;
        this.explanation = explanation;
        if (options != null) {
            this.options = options;
        }
    }
}
