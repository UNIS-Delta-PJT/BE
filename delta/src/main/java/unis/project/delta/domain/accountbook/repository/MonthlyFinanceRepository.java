package unis.project.delta.domain.accountbook.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import unis.project.delta.domain.accountbook.entity.MonthlyFinance;
import unis.project.delta.domain.user.entity.User;

import java.util.Optional;

public interface MonthlyFinanceRepository extends JpaRepository<MonthlyFinance, Long> {

    Optional<MonthlyFinance> findByUserAndTargetMonth(User user, String targetMonth);
}
