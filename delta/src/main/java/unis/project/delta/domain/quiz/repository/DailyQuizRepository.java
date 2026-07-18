package unis.project.delta.domain.quiz.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import unis.project.delta.domain.quiz.entity.DailyQuiz;

import java.util.Optional;

public interface DailyQuizRepository extends JpaRepository<DailyQuiz, Long> {

    /** 날짜 기반 순환 선택: offset = dayOfYear % count */
    @Query(value = "SELECT * FROM daily_quizzes LIMIT 1 OFFSET :offset", nativeQuery = true)
    Optional<DailyQuiz> findByOffset(@Param("offset") long offset);
}
