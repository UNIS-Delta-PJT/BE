package unis.project.delta.expense.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import unis.project.delta.category.domain.Category;
import unis.project.delta.category.repository.CategoryRepository;
import unis.project.delta.expense.domain.Expense;
import unis.project.delta.expense.dto.request.CreateExpenseRequest;
import unis.project.delta.expense.dto.response.CreateExpenseResponse;
import unis.project.delta.expense.dto.response.GetExpenseListResponse;
import unis.project.delta.expense.repository.ExpenseRepository;
import unis.project.delta.global.exception.CustomException;
import unis.project.delta.global.exception.ErrorCode;
import unis.project.delta.user.domain.User;
import unis.project.delta.user.repository.UserRepository;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    @Transactional
    public CreateExpenseResponse createExpense(String uuid, CreateExpenseRequest request) {
        User user = findByUuid(uuid);

        Category category = findByCategoryId(request.categoryId());

        // 유저의 카테고리 접근 권한 추가 검증
        if (category.getUser() != null && !category.getUser().getUserId().equals(user.getUserId())) {
            throw new CustomException(ErrorCode.USER_MISMATCH);
        }

        // 금액 검증
        if (request.amount() <= 0) {
            throw new CustomException(ErrorCode.INVALID_AMOUNT);
        }

        Expense expense = Expense.builder()
                .user(user)
                .category(category)
                .amount(request.amount())
                .expenseDate(LocalDate.parse(request.expenseDate())) // String -> LocalDate 변환
                .memo(request.memo())
                .build();

        Expense savedExpense = expenseRepository.save(expense);

        return CreateExpenseResponse.from(savedExpense);
    }

    @Transactional(readOnly = true)
    public GetExpenseListResponse getExpenseList(String uuid, String yearMonthStr) {
        User user = findByUuid(uuid);

        // 문자열 "YYYY-MM"를 자바 YearMonth 객체로 파싱
        java.time.YearMonth yearMonth = java.time.YearMonth.parse(yearMonthStr);

        // 해당 월의 1일과 마지막 일을 자동으로 계산
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        List<Expense> expenses = expenseRepository.findByUserAndExpenseDateBetween(user, startDate, endDate);

        return GetExpenseListResponse.of(yearMonthStr, expenses);
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