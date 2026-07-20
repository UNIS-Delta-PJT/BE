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
@Table(name = "user_daily_quiz_histories",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "target_date"}))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserDailyQuizHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "daily_quiz_id", nullable = false)
    private DailyQuiz dailyQuiz;

    @Column(nullable = false, length = 1)
    private String selectedAnswer;

    @Column(nullable = false)
    private Boolean isCorrect;

    @Column(nullable = false)
    private LocalDate targetDate;

    @Builder
    public UserDailyQuizHistory(User user, DailyQuiz dailyQuiz, String selectedAnswer,
                                Boolean isCorrect, LocalDate targetDate) {
        this.user = user;
        this.dailyQuiz = dailyQuiz;
        this.selectedAnswer = selectedAnswer;
        this.isCorrect = isCorrect;
        this.targetDate = targetDate;
    }
}
