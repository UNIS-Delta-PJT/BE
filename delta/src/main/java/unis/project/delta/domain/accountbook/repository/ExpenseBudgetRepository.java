package unis.project.delta.domain.accountbook.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import unis.project.delta.domain.accountbook.entity.ExpenseBudget;
import unis.project.delta.domain.accountbook.entity.MonthlyFinance;

import java.util.List;

public interface ExpenseBudgetRepository extends JpaRepository<ExpenseBudget, Long> {

    List<ExpenseBudget> findByMonthlyFinance(MonthlyFinance monthlyFinance);

    void deleteAllByMonthlyFinance(MonthlyFinance monthlyFinance);
}
