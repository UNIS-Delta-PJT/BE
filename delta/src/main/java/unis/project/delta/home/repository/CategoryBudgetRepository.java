package unis.project.delta.home.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import unis.project.delta.budget.domain.CategoryBudget;
import unis.project.delta.budget.domain.MonthlyBudget;

import java.util.List;

public interface CategoryBudgetRepository extends JpaRepository<CategoryBudget, Long> {

    List<CategoryBudget> findByMonthlyBudget(MonthlyBudget monthlyBudget);
}