package unis.project.delta.budget.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import unis.project.delta.budget.domain.MonthlyBudget;

public interface MonthlyBudgetRepository extends JpaRepository<MonthlyBudget, Long> {
}
