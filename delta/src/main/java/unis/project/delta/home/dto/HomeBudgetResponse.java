package unis.project.delta.home.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class HomeBudgetResponse {

    private String yearMonth;

    private Long totalAmount;

    private Long totalSpentAmount;

    private Long remainingAmount;

    private Double budgetUsageRate;
}