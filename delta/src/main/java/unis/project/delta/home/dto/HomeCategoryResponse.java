package unis.project.delta.home.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class HomeCategoryResponse {

    private Long categoryId;

    private String categoryName;

    private Long budgetAmount;

    private Long spentAmount;

    private Long remainingAmount;

    private Double usageRate;
}