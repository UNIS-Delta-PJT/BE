package unis.project.delta.domain.quiz.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import unis.project.delta.domain.quiz.entity.FinanceQuiz;

import java.util.Optional;

public interface FinanceQuizRepository extends JpaRepository<FinanceQuiz, Long> {

    /** 랜덤 퀴즈 1건 조회 */
    @Query(value = "SELECT * FROM finance_quizzes ORDER BY RAND() LIMIT 1", nativeQuery = true)
    Optional<FinanceQuiz> findOneRandom();
}
