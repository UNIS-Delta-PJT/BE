package unis.project.delta.domain.quiz.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import unis.project.delta.domain.quiz.entity.UserDailyQuizHistory;
import unis.project.delta.domain.user.entity.User;

import java.time.LocalDate;
import java.util.Optional;

public interface UserDailyQuizHistoryRepository extends JpaRepository<UserDailyQuizHistory, Long> {

    Optional<UserDailyQuizHistory> findByUserAndTargetDate(User user, LocalDate targetDate);

    boolean existsByUserAndTargetDate(User user, LocalDate targetDate);
}
