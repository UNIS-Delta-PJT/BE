package unis.project.delta.budget.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import unis.project.delta.budget.domain.CategoryBudget;
import unis.project.delta.budget.domain.MonthlyBudget;
import unis.project.delta.budget.dto.request.CreateBudgetRequest;
import unis.project.delta.budget.dto.response.CreateBudgetResponse;
import unis.project.delta.budget.repository.MonthlyBudgetRepository;
import unis.project.delta.category.domain.Category;
import unis.project.delta.category.repository.CategoryRepository;
import unis.project.delta.global.exception.CustomException;
import unis.project.delta.global.exception.ErrorCode;
import unis.project.delta.user.domain.User;
import unis.project.delta.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class MonthlyBudgetService {
    private final MonthlyBudgetRepository monthlyBudgetRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    @Transactional
    public CreateBudgetResponse createMonthlyBudget(String uuid, CreateBudgetRequest request) {
        User user = findByUuid(uuid);

        // 부모 객체인 MonthlyBudget 먼저 생성
        MonthlyBudget monthlyBudget = MonthlyBudget.builder()
                .yearMonth(request.yearMonth())
                .totalAmount(request.totalAmount())
                .user(user)
                .build();

        for (CreateBudgetRequest.CategoryBudgetDto dto : request.categoryBudgets()) {
            Category category = findByCategoryId(dto.categoryId());

            // 자식 객체인 CategoryBudget 생성
            CategoryBudget categoryBudget = CategoryBudget.builder()
                    .amount(dto.amount())
                    .category(category)
                    .build();

            monthlyBudget.addCategoryBudget(categoryBudget);
        }

        MonthlyBudget savedBudget = monthlyBudgetRepository.save(monthlyBudget);

        return CreateBudgetResponse.from(savedBudget);
    }

    private User findByUuid(String uuid) {
        return userRepository.findByUuid(uuid)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    private Category findByCategoryId(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));
    }
}
