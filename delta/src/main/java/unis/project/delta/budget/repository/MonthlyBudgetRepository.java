package unis.project.delta.budget.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import unis.project.delta.budget.domain.MonthlyBudget;
import unis.project.delta.user.domain.User;

import java.util.Optional;

public interface MonthlyBudgetRepository extends JpaRepository<MonthlyBudget, Long> {

    Optional<MonthlyBudget> findByUserAndYearMonth(User user, String yearMonth);
}
