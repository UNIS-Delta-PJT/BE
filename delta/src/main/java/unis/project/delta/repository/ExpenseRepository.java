package unis.project.delta.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import unis.project.delta.domain.Expense;
import unis.project.delta.domain.User;

import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findByUserAndExpenseDateBetween(
            User user,
            String startDate,
            String endDate
    );
}