package unis.project.delta.domain.quiz.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import unis.project.delta.domain.user.entity.User;

import java.time.LocalDate;

@Entity
@Getter
@Table(name = "user_finance_quiz_histories")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserFinanceQuizHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "finance_quiz_id", nullable = false)
    private FinanceQuiz financeQuiz;

    @Column(nullable = false)
    private Integer selectedOption;

    @Column(nullable = false)
    private Boolean isCorrect;

    @Column(nullable = false)
    private LocalDate targetDate;

    @Builder
    public UserFinanceQuizHistory(User user, FinanceQuiz financeQuiz, Integer selectedOption,
                                  Boolean isCorrect, LocalDate targetDate) {
        this.user = user;
        this.financeQuiz = financeQuiz;
        this.selectedOption = selectedOption;
        this.isCorrect = isCorrect;
        this.targetDate = targetDate;
    }
}
