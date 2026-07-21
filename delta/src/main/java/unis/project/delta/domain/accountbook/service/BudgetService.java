package unis.project.delta.domain.accountbook.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import unis.project.delta.domain.accountbook.dto.request.*;
import unis.project.delta.domain.accountbook.dto.response.*;
import unis.project.delta.domain.accountbook.entity.*;
import unis.project.delta.domain.accountbook.repository.*;
import unis.project.delta.domain.user.entity.User;
import unis.project.delta.domain.user.repository.UserRepository;
import unis.project.delta.global.exception.CustomException;
import unis.project.delta.global.exception.ErrorCode;

import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BudgetService {

    private final MonthlyFinanceRepository monthlyFinanceRepository;
    private final IncomeDetailRepository incomeDetailRepository;
    private final ExpenseBudgetRepository expenseBudgetRepository;
    private final ExpenseCategoryRepository expenseCategoryRepository;
    private final UserRepository userRepository;

    /**
     * 이번 달 예산 설정 전체 현황(수입, 저축, 지출 예산)을 조회한다.
     */
    @Transactional(readOnly = true)
    public BudgetOverviewResponse getBudgetOverview(Long userId) {
        User user = findByUserId(userId);
        MonthlyFinance mf = findCurrentMonthlyFinance(user);

        List<IncomeDetail> incomes = incomeDetailRepository.findByMonthlyFinance(mf);
        List<ExpenseBudget> budgets = expenseBudgetRepository.findByMonthlyFinance(mf);

        return BudgetOverviewResponse.from(mf, incomes, budgets);
    }

    /**
     * 이번 달 카테고리별 수입 내역을 전체 교체(덮어쓰기)한다.
     */
    @Transactional
    public UpdateIncomeResponse updateIncome(Long userId, UpdateIncomeRequest request) {
        User user = findByUserId(userId);
        MonthlyFinance mf = findCurrentMonthlyFinance(user);

        // 기존 수입 내역 삭제
        incomeDetailRepository.deleteAllByMonthlyFinance(mf);
        incomeDetailRepository.flush();

        // 새 수입 내역 저장 및 합계 계산
        long totalIncome = 0;
        for (IncomeItemRequest item : request.getIncomeDetails()) {
            IncomeDetail detail = IncomeDetail.builder()
                    .monthlyFinance(mf)
                    .category(item.getCategory())
                    .amount(item.getAmount())
                    .build();
            incomeDetailRepository.save(detail);
            totalIncome += item.getAmount();
        }

        // MonthlyFinance 총 수입 갱신
        mf.updateIncome(totalIncome);

        return UpdateIncomeResponse.from(totalIncome);
    }

    /**
     * 이번 달 저축 목표 금액을 수정한다.
     * 저축 유형은 총 수입 대비 비율로 자동 결정된다.
     */
    @Transactional
    public UpdateSavingsResponse updateSavings(Long userId, UpdateSavingsRequest request) {
        User user = findByUserId(userId);
        MonthlyFinance mf = findCurrentMonthlyFinance(user);

        mf.updateTargetSavings(request.getTargetSavings());

        SavingsType savingsType = calculateSavingsType(request.getTargetSavings(), mf.getTotalIncome());
        mf.updateSavingsType(savingsType);

        return UpdateSavingsResponse.of(mf.getTargetSavings(), savingsType.name());
    }

    /**
     * 한 달 목표 총 지출 예산과 카테고리별 목표 지출 예산을 수정한다.
     * 카테고리별 합계가 총 예산과 일치해야 한다.
     */
    @Transactional
    public void updateExpenseBudget(Long userId, UpdateExpenseBudgetRequest request) {
        User user = findByUserId(userId);
        MonthlyFinance mf = findCurrentMonthlyFinance(user);

        // 합계 검증
        long categorySum = request.getExpenseBudgets().stream()
                .mapToLong(BudgetItemRequest::getAmount)
                .sum();

        if (categorySum != request.getTotalExpenseBudget()) {
            throw new CustomException(ErrorCode.BUDGET_SUM_MISMATCH);
        }

        // 총 지출 예산 갱신
        mf.updateExpenseBudget(request.getTotalExpenseBudget());

        // 기존 카테고리별 예산 삭제
        expenseBudgetRepository.deleteAllByMonthlyFinance(mf);
        expenseBudgetRepository.flush();

        // 새 카테고리별 예산 저장
        for (BudgetItemRequest item : request.getExpenseBudgets()) {
            ExpenseCategory category = expenseCategoryRepository.findById(item.getCategoryId())
                    .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));

            ExpenseBudget budget = ExpenseBudget.builder()
                    .monthlyFinance(mf)
                    .expenseCategory(category)
                    .amount(item.getAmount())
                    .build();
            expenseBudgetRepository.save(budget);
        }
    }

    /**
     * 지난달의 카테고리별 목표 지출 예산을 조회한다.
     */
    @Transactional(readOnly = true)
    public CopyLastMonthResponse copyLastMonth(Long userId) {
        User user = findByUserId(userId);

        String lastMonth = YearMonth.now().minusMonths(1).toString();

        MonthlyFinance lastMf = monthlyFinanceRepository.findByUserAndTargetMonth(user, lastMonth)
                .orElseThrow(() -> new CustomException(ErrorCode.MONTHLY_FINANCE_NOT_FOUND));

        List<ExpenseBudget> budgets = expenseBudgetRepository.findByMonthlyFinance(lastMf);

        return CopyLastMonthResponse.of(lastMonth, lastMf.getTotalExpenseBudget(), budgets);
    }

    /**
     * 기본 카테고리 + 사용자 커스텀 카테고리를 조회한다.
     */
    @Transactional(readOnly = true)
    public CategoryListResponse getCategories(Long userId) {
        User user = findByUserId(userId);

        List<ExpenseCategory> categories = expenseCategoryRepository.findByUserIsNullOrUser(user);

        return CategoryListResponse.from(categories);
    }

    /**
     * 사용자 커스텀 지출 카테고리를 추가한다.
     */
    @Transactional
    public CategoryResponse createCategory(Long userId, CreateCategoryRequest request) {
        User user = findByUserId(userId);

        // 중복 이름 검사 (기본 카테고리 + 해당 사용자의 커스텀 카테고리)
        if (expenseCategoryRepository.existsByNameAndUserIsNull(request.getName()) ||
                expenseCategoryRepository.existsByNameAndUser(request.getName(), user)) {
            throw new CustomException(ErrorCode.DUPLICATE_CATEGORY_NAME);
        }

        ExpenseCategory category = ExpenseCategory.builder()
                .user(user)
                .name(request.getName())
                .isDefault(false)
                .build();

        expenseCategoryRepository.save(category);

        return CategoryResponse.from(category);
    }

    /**
     * 사용자 커스텀 지출 카테고리를 삭제한다.
     * 기본 제공 카테고리는 삭제할 수 없다.
     */
    @Transactional
    public void deleteCategory(Long userId, Long categoryId) {
        ExpenseCategory category = expenseCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));

        if (category.getIsDefault()) {
            throw new CustomException(ErrorCode.DEFAULT_CATEGORY_NOT_MODIFIABLE);
        }

        expenseCategoryRepository.delete(category);
    }

    // ── private helpers ──

    private User findByUserId(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    private MonthlyFinance findCurrentMonthlyFinance(User user) {
        String currentMonth = YearMonth.now().toString();
        return monthlyFinanceRepository.findByUserAndTargetMonth(user, currentMonth)
                .orElseGet(() -> monthlyFinanceRepository.save(
                        MonthlyFinance.builder()
                                .user(user)
                                .targetMonth(currentMonth)
                                .totalIncome(0L)
                                .targetSavings(0L)
                                .totalExpenseBudget(0L)
                                .savingsType(SavingsType.SAVING)
                                .build()
                ));
    }

    /**
     * 총 수입 대비 저축 목표 비율로 저축 유형을 자동 결정한다.
     * 0~30%: SAVING(절약형), ~50%: STANDARD(표준형), 50% 초과: CHALLENGE(도전형)
     */
    private SavingsType calculateSavingsType(Long targetSavings, Long totalIncome) {
        if (totalIncome == 0) {
            return SavingsType.SAVING;
        }
        double rate = (double) targetSavings / totalIncome * 100;
        if (rate <= 30) return SavingsType.SAVING;
        else if (rate <= 50) return SavingsType.STANDARD;
        else return SavingsType.CHALLENGE;
    }
}
