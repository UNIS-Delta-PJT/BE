package unis.project.delta.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import unis.project.delta.domain.MonthlyBudget;
import unis.project.delta.domain.User;

import java.util.Optional;

public interface MonthlyBudgetRepository extends JpaRepository<MonthlyBudget, Long> {

    Optional<MonthlyBudget> findByUserAndYearMonth(User user, String yearMonth);
}