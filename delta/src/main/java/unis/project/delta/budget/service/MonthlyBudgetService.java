package unis.project.delta.budget.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import unis.project.delta.budget.domain.CategoryBudget;
import unis.project.delta.budget.domain.MonthlyBudget;
import unis.project.delta.budget.dto.request.CreateBudgetRequest;
import unis.project.delta.budget.dto.response.MonthlyBudgetResponse;
import unis.project.delta.budget.repository.MonthlyBudgetRepository;
import unis.project.delta.category.domain.Category;
import unis.project.delta.category.repository.CategoryRepository;
import unis.project.delta.global.exception.CustomException;
import unis.project.delta.global.exception.ErrorCode;
import unis.project.delta.user.domain.User;
import unis.project.delta.user.repository.UserRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MonthlyBudgetService {
    private final MonthlyBudgetRepository monthlyBudgetRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    // 1. 원예산 등록
    @Transactional
    public MonthlyBudgetResponse createMonthlyBudget(String uuid, CreateBudgetRequest request) {
        User user = findByUuid(uuid);

        // 카테고리별 예산 총합 일치 여부 확인
        validateBudgetAmountMismatch(request);

        // 이미 해당 연월에 등록한 예산이 확인
        Optional<MonthlyBudget> existingBudgetOpt = monthlyBudgetRepository.findByUserAndYearMonth(user, request.yearMonth());

        // 2) 이미 예산이 존재하면 기존 예산에 금액을 누적 업데이트
        if (existingBudgetOpt.isPresent()) {
            MonthlyBudget existingBudget = existingBudgetOpt.get();
            return updateAndAccumulateBudget(existingBudget, request);
        }

        // 1) 최초 등록이면 새로 insert
        return saveNewMonthlyBudget(user, request);
    }

    // 1-1. 예산 최초 등록
    private MonthlyBudgetResponse saveNewMonthlyBudget(User user, CreateBudgetRequest request) {
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
        return MonthlyBudgetResponse.from(savedBudget);
    }

    // 1-2. 재등록시 누적 업데이트
    private MonthlyBudgetResponse updateAndAccumulateBudget(MonthlyBudget budget, CreateBudgetRequest request) {
        // 월 전체 총액 누적
        budget.updateTotalAmount(budget.getTotalAmount() + request.totalAmount());

        // 카테고리별 예산 누적
        for (CreateBudgetRequest.CategoryBudgetDto dto : request.categoryBudgets()) {

            Optional<CategoryBudget> existingCategoryBudget = budget.getCategoryBudgets().stream()
                    .filter(cb -> cb.getCategory().getCategoryId().equals(dto.categoryId()))
                    .findFirst();

            if (existingCategoryBudget.isPresent()) {
                CategoryBudget targetCb = existingCategoryBudget.get();
                targetCb.updateAmount(targetCb.getAmount() + dto.amount());
            } else {
                Category category = findByCategoryId(dto.categoryId());
                CategoryBudget newCb = CategoryBudget.builder()
                        .amount(dto.amount())
                        .category(category)
                        .build();
                budget.addCategoryBudget(newCb);
            }
        }

        return MonthlyBudgetResponse.from(budget);
    }

    // TODO: 2. 월예산 조회
    @Transactional(readOnly = true)
    public MonthlyBudgetResponse getMonthlyBudget(String uuid, String yearMonth) {
        User user = findByUuid(uuid);
        MonthlyBudget monthlyBudget = findByYearMonth(user, yearMonth);

        return MonthlyBudgetResponse.from(monthlyBudget);
    }


    // TODO: 3. 월예산 수정


    // ===== 헬퍼 함수 =====
    private User findByUuid(String uuid) {
        return userRepository.findByUuid(uuid)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    private Category findByCategoryId(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));
    }

    private MonthlyBudget findByYearMonth(User user, String yearMonth) {
        return monthlyBudgetRepository.findByUserAndYearMonth(user, yearMonth)
                .orElseThrow(() -> new CustomException(ErrorCode.MONTHLY_BUDGET_NOT_FOUND));
    }

    // 등록 예산과 카테고리 총합 검증 메서드
    private void validateBudgetAmountMismatch(CreateBudgetRequest request) {
        long sumOfCategoryBudgets = request.categoryBudgets().stream()
                .mapToLong(CreateBudgetRequest.CategoryBudgetDto::amount)
                .sum();

        // 일치하지 않으면 예외 발생
        if (request.totalAmount() != sumOfCategoryBudgets) {
            throw new CustomException(ErrorCode.CATEGORY_BUDGET_MISMATCH);
        }
    }
}
