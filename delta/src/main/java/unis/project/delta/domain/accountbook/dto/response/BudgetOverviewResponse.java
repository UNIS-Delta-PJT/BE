package unis.project.delta.domain.accountbook.dto.response;

import unis.project.delta.domain.accountbook.entity.ExpenseBudget;
import unis.project.delta.domain.accountbook.entity.IncomeDetail;
import unis.project.delta.domain.accountbook.entity.MonthlyFinance;

import java.util.List;

public record BudgetOverviewResponse(
        String targetMonth,
        Long totalIncome,
        Long targetSavings,
        String savingsType,
        Long totalExpenseBudget,
        List<IncomeDetailItemResponse> incomeDetails,
        List<ExpenseBudgetItemResponse> expenseBudgets
) {
    public static BudgetOverviewResponse from(MonthlyFinance mf,
                                              List<IncomeDetail> incomes,
                                              List<ExpenseBudget> budgets) {
        return new BudgetOverviewResponse(
                mf.getTargetMonth(),
                mf.getTotalIncome(),
                mf.getTargetSavings(),
                mf.getSavingsType().name(),
                mf.getTotalExpenseBudget(),
                incomes.stream().map(IncomeDetailItemResponse::from).toList(),
                budgets.stream().map(ExpenseBudgetItemResponse::from).toList()
        );
    }
}
