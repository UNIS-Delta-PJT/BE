package unis.project.delta.home.service;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import unis.project.delta.domain.CategoryBudget;
import unis.project.delta.domain.CharacterProfile;
import unis.project.delta.domain.Expense;
import unis.project.delta.domain.MonthlyBudget;
import unis.project.delta.domain.User;
import unis.project.delta.home.dto.HomeBudgetResponse;
import unis.project.delta.home.dto.HomeCategoryResponse;
import unis.project.delta.home.dto.HomeCharacterResponse;
import unis.project.delta.home.dto.HomeResponse;
import unis.project.delta.home.dto.HomeUserResponse;
import unis.project.delta.home.dto.RecentExpenseResponse;
import unis.project.delta.repository.CategoryBudgetRepository;
import unis.project.delta.repository.CharacterProfileRepository;
import unis.project.delta.repository.ExpenseRepository;
import unis.project.delta.repository.MonthlyBudgetRepository;
import unis.project.delta.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class HomeService {

    private final UserRepository userRepository;
    private final MonthlyBudgetRepository monthlyBudgetRepository;
    private final CategoryBudgetRepository categoryBudgetRepository;
    private final ExpenseRepository expenseRepository;
    private final CharacterProfileRepository characterProfileRepository;

    public HomeResponse getHome(String authorization, String yearMonth) {
        String uuid = extractUuid(authorization);

        User user = userRepository.findByUuid(uuid)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 사용자입니다."));

        MonthlyBudget monthlyBudget = monthlyBudgetRepository
                .findByUserAndYearMonth(user, yearMonth)
                .orElseThrow(() -> new RuntimeException("해당 월의 예산이 존재하지 않습니다."));

        List<CategoryBudget> categoryBudgets =
                categoryBudgetRepository.findByMonthlyBudget(monthlyBudget);

        List<Expense> expenses =
                expenseRepository.findByUserAndExpenseDateBetween(
                        user,
                        yearMonth + "-01",
                        yearMonth + "-31"
                );

        long totalSpentAmount = expenses.stream()
                .mapToLong(Expense::getAmount)
                .sum();

        long remainingAmount = monthlyBudget.getTotalAmount() - totalSpentAmount;

        double budgetUsageRate = calculateRate(
                totalSpentAmount,
                monthlyBudget.getTotalAmount()
        );

        List<HomeCategoryResponse> categoryResponses = categoryBudgets.stream()
                .map(categoryBudget -> {
                    long spentAmount = expenses.stream()
                            .filter(expense -> expense.getCategory().getCategoryId()
                                    .equals(categoryBudget.getCategory().getCategoryId()))
                            .mapToLong(Expense::getAmount)
                            .sum();

                    long categoryRemainingAmount =
                            categoryBudget.getAmount() - spentAmount;

                    double usageRate = calculateRate(
                            spentAmount,
                            categoryBudget.getAmount()
                    );

                    return HomeCategoryResponse.builder()
                            .categoryId(categoryBudget.getCategory().getCategoryId())
                            .categoryName(categoryBudget.getCategory().getName())
                            .budgetAmount(categoryBudget.getAmount())
                            .spentAmount(spentAmount)
                            .remainingAmount(categoryRemainingAmount)
                            .usageRate(usageRate)
                            .build();
                })
                .toList();

        List<RecentExpenseResponse> recentExpenses = expenses.stream()
                .sorted(Comparator.comparing(Expense::getExpenseDate).reversed())
                .limit(5)
                .map(expense -> RecentExpenseResponse.builder()
                        .expenseId(expense.getExpenseId())
                        .categoryName(expense.getCategory().getName())
                        .amount(expense.getAmount())
                        .expenseDate(expense.getExpenseDate())
                        .memo(expense.getMemo())
                        .build())
                .toList();

        CharacterProfile character = characterProfileRepository.findByUser(user)
                .orElse(null);

        HomeCharacterResponse characterResponse = null;

        if (character != null) {
            characterResponse = HomeCharacterResponse.builder()
                    .characterId(character.getCharacterId())
                    .name(character.getName())
                    .feelingXp(character.getFeelingXp())
                    .coin(character.getCoin())
                    .build();
        }

        return HomeResponse.builder()
                .user(HomeUserResponse.builder()
                        .userId(user.getUserId())
                        .nickname(user.getNickname())
                        .build())
                .budget(HomeBudgetResponse.builder()
                        .yearMonth(yearMonth)
                        .totalAmount(monthlyBudget.getTotalAmount())
                        .totalSpentAmount(totalSpentAmount)
                        .remainingAmount(remainingAmount)
                        .budgetUsageRate(budgetUsageRate)
                        .build())
                .categories(categoryResponses)
                .character(characterResponse)
                .recentExpenses(recentExpenses)
                .aiMessage(createAiMessage(budgetUsageRate))
                .build();
    }

    private String extractUuid(String authorization) {
        if (authorization == null || authorization.isBlank()) {
            throw new RuntimeException("인증 헤더(UUID)가 누락되었습니다.");
        }

        if (authorization.startsWith("Bearer ")) {
            return authorization.substring(7);
        }

        return authorization;
    }

    private double calculateRate(long usedAmount, long totalAmount) {
        if (totalAmount == 0) {
            return 0.0;
        }

        return Math.round((usedAmount * 1000.0 / totalAmount)) / 10.0;
    }

    private String createAiMessage(double budgetUsageRate) {
    	return "AI 조언";
    }
}