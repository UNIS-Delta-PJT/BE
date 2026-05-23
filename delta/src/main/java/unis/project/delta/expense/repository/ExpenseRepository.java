package unis.project.delta.expense.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import unis.project.delta.expense.domain.Expense;
import unis.project.delta.user.domain.User;

import java.time.LocalDate;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    // 특정 유저의 시작일과 종료일 사이(한 달 치) 지출 내역을 찾아오기
    List<Expense> findByUserAndExpenseDateBetween(User user, LocalDate startDate, LocalDate endDate);
}