package unis.project.delta.domain.quiz.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "daily_quizzes")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DailyQuiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String question;

    /** 정답: "O" 또는 "X" */
    @Column(nullable = false, length = 1)
    private String correctAnswer;

    @Column(nullable = false, length = 1000)
    private String explanation;

    @Builder
    public DailyQuiz(String question, String correctAnswer, String explanation) {
        this.question = question;
        this.correctAnswer = correctAnswer;
        this.explanation = explanation;
    }
}
