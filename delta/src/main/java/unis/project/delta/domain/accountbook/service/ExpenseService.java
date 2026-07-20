package unis.project.delta.domain.accountbook.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import unis.project.delta.domain.accountbook.dto.request.CreateExpensesRequest;
import unis.project.delta.domain.accountbook.dto.request.ExpenseItemRequest;
import unis.project.delta.domain.accountbook.dto.response.BudgetSummaryResponse;
import unis.project.delta.domain.accountbook.dto.response.DailyExpenseResponse;
import unis.project.delta.domain.accountbook.dto.response.ExpenseCreateResponse;
import unis.project.delta.domain.accountbook.entity.ExpenseCategory;
import unis.project.delta.domain.accountbook.entity.ExpenseRecord;
import unis.project.delta.domain.accountbook.entity.MonthlyFinance;
import unis.project.delta.domain.accountbook.repository.ExpenseCategoryRepository;
import unis.project.delta.domain.accountbook.repository.ExpenseRecordRepository;
import unis.project.delta.domain.accountbook.repository.MonthlyFinanceRepository;
import unis.project.delta.domain.mission.entity.DailyMission;
import unis.project.delta.domain.mission.repository.DailyMissionRepository;
import unis.project.delta.domain.user.entity.User;
import unis.project.delta.domain.user.repository.UserRepository;
import unis.project.delta.global.exception.CustomException;
import unis.project.delta.global.exception.ErrorCode;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRecordRepository expenseRecordRepository;
    private final ExpenseCategoryRepository expenseCategoryRepository;
    private final MonthlyFinanceRepository monthlyFinanceRepository;
    private final DailyMissionRepository dailyMissionRepository;
    private final UserRepository userRepository;

    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    /**
     * 지출 내역을 직접 입력한다.
     * 해당 날짜의 첫 기록이면 오늘의 미션(지출 기록)을 달성 처리한다.
     */
    @Transactional
    public ExpenseCreateResponse createExpenses(Long userId, CreateExpensesRequest request) {
        User user = findByUserId(userId);

        // 첫 번째 지출 항목의 날짜 기준으로 첫 기록 여부 판단
        LocalDate expenseDate = LocalDateTime.parse(
                request.getExpenses().get(0).getExpenseDate(), DATE_TIME_FORMAT).toLocalDate();
        LocalDateTime startOfDay = expenseDate.atStartOfDay();
        LocalDateTime endOfDay = expenseDate.plusDays(1).atStartOfDay();

        boolean isFirstRecordOfDay = !expenseRecordRepository
                .existsByUserAndExpenseDateBetween(user, startOfDay, endOfDay);

        // 지출 내역 저장
        for (ExpenseItemRequest item : request.getExpenses()) {
            ExpenseCategory category = expenseCategoryRepository.findById(item.getCategoryId())
                    .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));

            LocalDateTime dateTime = LocalDateTime.parse(item.getExpenseDate(), DATE_TIME_FORMAT);

            ExpenseRecord record = ExpenseRecord.builder()
                    .user(user)
                    .expenseCategory(category)
                    .amount(item.getAmount())
                    .placeName(item.getPlaceName())
                    .expenseDate(dateTime)
                    .memo(item.getMemo())
                    .build();

            expenseRecordRepository.save(record);
        }

        // 첫 기록이면 지출 기록 미션 달성 처리
        if (isFirstRecordOfDay) {
            completeDailyExpenseMission(user, expenseDate);
        }

        // 해당 날짜 총 지출 재계산
        Long dailyTotal = expenseRecordRepository.sumAmountByUserAndDateRange(user, startOfDay, endOfDay);

        return ExpenseCreateResponse.of(request.getExpenses().size(), isFirstRecordOfDay, dailyTotal);
    }

    /**
     * 특정 날짜의 소비 내역 리스트와 총 지출액을 조회한다.
     */
    @Transactional(readOnly = true)
    public DailyExpenseResponse getDailyExpenses(Long userId, LocalDate date) {
        User user = findByUserId(userId);

        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();

        List<ExpenseRecord> records = expenseRecordRepository
                .findByUserAndExpenseDateBetweenOrderByExpenseDateAsc(user, startOfDay, endOfDay);

        Long dailyTotal = expenseRecordRepository.sumAmountByUserAndDateRange(user, startOfDay, endOfDay);

        return DailyExpenseResponse.of(date, dailyTotal, records);
    }

    /**
     * 이번 달 예산 요약(남은 예산, 사용 금액, 소진율)을 조회한다.
     */
    @Transactional(readOnly = true)
    public BudgetSummaryResponse getBudgetSummary(Long userId) {
        User user = findByUserId(userId);

        String currentMonth = YearMonth.now().toString();

        MonthlyFinance mf = monthlyFinanceRepository.findByUserAndTargetMonth(user, currentMonth)
                .orElseThrow(() -> new CustomException(ErrorCode.MONTHLY_FINANCE_NOT_FOUND));

        // 이번 달 총 지출 계산
        YearMonth ym = YearMonth.parse(currentMonth);
        LocalDateTime monthStart = ym.atDay(1).atStartOfDay();
        LocalDateTime monthEnd = ym.plusMonths(1).atDay(1).atStartOfDay();

        Long totalSpent = expenseRecordRepository.sumAmountByUserAndDateRange(user, monthStart, monthEnd);

        return BudgetSummaryResponse.of(currentMonth, mf.getTotalExpenseBudget(), totalSpent);
    }

    // ── private helpers ──

    private User findByUserId(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    private void completeDailyExpenseMission(User user, LocalDate date) {
        DailyMission mission = dailyMissionRepository.findByUserAndTargetDate(user, date)
                .orElseGet(() -> dailyMissionRepository.save(
                        DailyMission.builder()
                                .user(user)
                                .targetDate(date)
                                .build()
                ));
        mission.completeExpense();
    }
}
