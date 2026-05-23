package unis.project.delta.home.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class HomeResponse {

    private HomeUserResponse user;

    private HomeBudgetResponse budget;

    private List<HomeCategoryResponse> categories;

    private HomeCharacterResponse character;

    private List<RecentExpenseResponse> recentExpenses;

    private String aiMessage;
}