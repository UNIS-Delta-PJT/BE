package unis.project.delta.domain.quiz.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import unis.project.delta.domain.quiz.entity.UserFinanceQuizHistory;
import unis.project.delta.domain.user.entity.User;

import java.time.LocalDate;

public interface UserFinanceQuizHistoryRepository extends JpaRepository<UserFinanceQuizHistory, Long> {

    /** 오늘 금융 퀴즈를 정답으로 맞힌 이력이 있는지 확인 (주사위 활성화 판단) */
    boolean existsByUserAndTargetDateAndIsCorrectTrue(User user, LocalDate targetDate);
}
