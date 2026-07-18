package unis.project.delta.domain.accountbook.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import unis.project.delta.domain.accountbook.entity.IncomeDetail;
import unis.project.delta.domain.accountbook.entity.MonthlyFinance;

import java.util.List;

public interface IncomeDetailRepository extends JpaRepository<IncomeDetail, Long> {

    List<IncomeDetail> findByMonthlyFinance(MonthlyFinance monthlyFinance);

    void deleteAllByMonthlyFinance(MonthlyFinance monthlyFinance);
}
