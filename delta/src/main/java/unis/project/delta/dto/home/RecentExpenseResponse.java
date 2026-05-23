package unis.project.delta.dto.home;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class RecentExpenseResponse {

    private Long expenseId;

    private String categoryName;

    private Long amount;

    private String expenseDate;

    private String memo;
}