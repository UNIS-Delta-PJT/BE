package unis.project.delta.budget.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import unis.project.delta.budget.domain.CategoryBudget;

public interface CategoryBudgetRepository extends JpaRepository<CategoryBudget, Long> {
}
