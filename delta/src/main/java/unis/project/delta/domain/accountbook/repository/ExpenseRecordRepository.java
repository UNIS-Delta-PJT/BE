package unis.project.delta.domain.accountbook.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import unis.project.delta.domain.accountbook.entity.ExpenseRecord;
import unis.project.delta.domain.user.entity.User;

import java.time.LocalDateTime;
import java.util.List;

public interface ExpenseRecordRepository extends JpaRepository<ExpenseRecord, Long> {

    List<ExpenseRecord> findByUserAndExpenseDateBetweenOrderByExpenseDateAsc(
            User user, LocalDateTime start, LocalDateTime end);

    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM ExpenseRecord e " +
            "WHERE e.user = :user AND e.expenseDate >= :start AND e.expenseDate < :end")
    Long sumAmountByUserAndDateRange(
            @Param("user") User user,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    boolean existsByUserAndExpenseDateBetween(User user, LocalDateTime start, LocalDateTime end);
}
