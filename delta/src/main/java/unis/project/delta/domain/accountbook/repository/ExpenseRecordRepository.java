package unis.project.delta.domain.accountbook.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import unis.project.delta.domain.accountbook.entity.ExpenseRecord;
import unis.project.delta.domain.user.entity.User;

import java.time.LocalDateTime;
import java.util.List;

public interface ExpenseRecordRepository extends JpaRepository<ExpenseRecord, Long> {

    // ── 기존 (Finance 도메인) ──

    List<ExpenseRecord> findByUserAndExpenseDateBetweenOrderByExpenseDateAsc(
            User user, LocalDateTime start, LocalDateTime end);

    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM ExpenseRecord e " +
            "WHERE e.user = :user AND e.expenseDate >= :start AND e.expenseDate < :end")
    Long sumAmountByUserAndDateRange(
            @Param("user") User user,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    boolean existsByUserAndExpenseDateBetween(User user, LocalDateTime start, LocalDateTime end);

    // ── 신규 (Report 도메인 집계용) ──

    /** 카테고리별 지출 합계 (categoryId, categoryName, sumAmount) */
    @Query("SELECT e.expenseCategory.id, e.expenseCategory.name, SUM(e.amount) " +
            "FROM ExpenseRecord e " +
            "WHERE e.user = :user AND e.expenseDate >= :start AND e.expenseDate < :end " +
            "GROUP BY e.expenseCategory.id, e.expenseCategory.name " +
            "ORDER BY SUM(e.amount) DESC")
    List<Object[]> findCategoryExpenseSums(
            @Param("user") User user,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    /** 금액 기준 상위 N건 지출 내역 */
    @Query("SELECT e FROM ExpenseRecord e " +
            "JOIN FETCH e.expenseCategory " +
            "WHERE e.user = :user AND e.expenseDate >= :start AND e.expenseDate < :end " +
            "ORDER BY e.amount DESC")
    List<ExpenseRecord> findTopExpenses(
            @Param("user") User user,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    /** 또래 비교: 해당 기간에 지출이 있는 전체 사용자 수 */
    @Query("SELECT COUNT(DISTINCT e.user.id) FROM ExpenseRecord e " +
            "WHERE e.expenseDate >= :start AND e.expenseDate < :end")
    Long countDistinctUsersByDateRange(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    /** 또래 비교: 해당 기간에 현재 사용자보다 지출이 많은 사용자 수 */
    // 🌟 쿼리문 내 e.user.id 뒤에 'AS userId'를 추가하여 하이버네이트 오류를 해결했습니다.
    @Query("SELECT COUNT(*) FROM (" +
            "  SELECT e.user.id AS userId FROM ExpenseRecord e " +
            "  WHERE e.expenseDate >= :start AND e.expenseDate < :end " +
            "  GROUP BY e.user.id " +
            "  HAVING SUM(e.amount) > :userTotal" +
            ") sub")
    Long countUsersWithMoreSpending(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("userTotal") Long userTotal);
}