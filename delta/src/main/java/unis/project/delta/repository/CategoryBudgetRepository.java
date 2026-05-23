package unis.project.delta.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import unis.project.delta.domain.CategoryBudget;
import unis.project.delta.domain.MonthlyBudget;

import java.util.List;

public interface CategoryBudgetRepository extends JpaRepository<CategoryBudget, Long> {

    List<CategoryBudget> findByMonthlyBudget(MonthlyBudget monthlyBudget);
}