package unis.project.delta.domain.accountbook.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import unis.project.delta.domain.accountbook.dto.request.*;
import unis.project.delta.domain.accountbook.dto.response.*;
import unis.project.delta.domain.accountbook.service.BudgetService;
import unis.project.delta.domain.accountbook.service.ExpenseService;
import unis.project.delta.global.response.ApiResponse;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/finances")
public class FinanceController {

    private final ExpenseService expenseService;
    private final BudgetService budgetService;

    // ════════════════════════════════════════
    //  소비 (Expense)
    // ════════════════════════════════════════

    /**
     * 오늘의 소비 직접 입력.
     */
    @PostMapping("/expenses")
    public ResponseEntity<ApiResponse<ExpenseCreateResponse>> createExpenses(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody CreateExpensesRequest request) {

        ExpenseCreateResponse response = expenseService.createExpenses(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "지출 기록 저장 성공"));
    }

    /**
     * 특정 날짜의 소비 내역 리스트 및 총 지출액 조회.
     */
    @GetMapping("/expenses/daily")
    public ResponseEntity<ApiResponse<DailyExpenseResponse>> getDailyExpenses(
            @AuthenticationPrincipal Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        DailyExpenseResponse response = expenseService.getDailyExpenses(userId, date);
        return ResponseEntity.ok(ApiResponse.success(response, "일별 소비 내역 조회 성공"));
    }

    /**
     * 홈 화면 예산 요약 조회.
     */
    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<BudgetSummaryResponse>> getBudgetSummary(
            @AuthenticationPrincipal Long userId) {

        BudgetSummaryResponse response = expenseService.getBudgetSummary(userId);
        return ResponseEntity.ok(ApiResponse.success(response, "예산 요약 조회 성공"));
    }

    // ════════════════════════════════════════
    //  예산 설정 (Budget)
    // ════════════════════════════════════════

    /**
     * 이번 달 예산 설정 전체 현황 조회.
     */
    @GetMapping("/budget")
    public ResponseEntity<ApiResponse<BudgetOverviewResponse>> getBudgetOverview(
            @AuthenticationPrincipal Long userId) {

        BudgetOverviewResponse response = budgetService.getBudgetOverview(userId);
        return ResponseEntity.ok(ApiResponse.success(response, "예산 설정 현황 조회 성공"));
    }

    /**
     * 이번 달 카테고리별 수입 총합 및 내역 수정.
     */
    @PutMapping("/income")
    public ResponseEntity<ApiResponse<UpdateIncomeResponse>> updateIncome(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody UpdateIncomeRequest request) {

        UpdateIncomeResponse response = budgetService.updateIncome(userId, request);
        return ResponseEntity.ok(ApiResponse.success(response, "수입 내역 수정 성공"));
    }

    /**
     * 이번 달 저축 목표 금액 및 저축 유형 수정.
     */
    @PutMapping("/savings")
    public ResponseEntity<ApiResponse<UpdateSavingsResponse>> updateSavings(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody UpdateSavingsRequest request) {

        UpdateSavingsResponse response = budgetService.updateSavings(userId, request);
        return ResponseEntity.ok(ApiResponse.success(response, "저축 목표 수정 성공"));
    }

    /**
     * 한 달 목표 총 지출 예산 및 카테고리별 목표 지출 예산 수정.
     */
    @PutMapping("/expense-budget")
    public ResponseEntity<ApiResponse<Void>> updateExpenseBudget(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody UpdateExpenseBudgetRequest request) {

        budgetService.updateExpenseBudget(userId, request);
        return ResponseEntity.ok(ApiResponse.success("지출 예산 수정 성공"));
    }

    /**
     * 지난달 예산 계획 복사 데이터 조회.
     */
    @GetMapping("/expense-budget/copy-last-month")
    public ResponseEntity<ApiResponse<CopyLastMonthResponse>> copyLastMonth(
            @AuthenticationPrincipal Long userId) {

        CopyLastMonthResponse response = budgetService.copyLastMonth(userId);
        return ResponseEntity.ok(ApiResponse.success(response, "지난달 예산 계획 조회 성공"));
    }

    // ════════════════════════════════════════
    //  지출 카테고리 (Expense Category)
    // ════════════════════════════════════════

    /**
     * 지출 카테고리 목록 조회.
     */
    @GetMapping("/expense-categories")
    public ResponseEntity<ApiResponse<CategoryListResponse>> getCategories(
            @AuthenticationPrincipal Long userId) {

        CategoryListResponse response = budgetService.getCategories(userId);
        return ResponseEntity.ok(ApiResponse.success(response, "지출 카테고리 목록 조회 성공"));
    }

    /**
     * 사용자 커스텀 지출 카테고리 추가.
     */
    @PostMapping("/expense-categories")
    public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody CreateCategoryRequest request) {

        CategoryResponse response = budgetService.createCategory(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "카테고리 추가 성공"));
    }

    /**
     * 사용자 커스텀 지출 카테고리 삭제.
     */
    @DeleteMapping("/expense-categories/{categoryId}")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(
            @AuthenticationPrincipal Long userId,
            @PathVariable("categoryId") Long categoryId) {

        budgetService.deleteCategory(userId, categoryId);
        return ResponseEntity.ok(ApiResponse.success("카테고리 삭제 성공"));
    }
}
