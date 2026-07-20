package unis.project.delta.domain.report.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import unis.project.delta.domain.accountbook.entity.ExpenseBudget;
import unis.project.delta.domain.accountbook.entity.ExpenseRecord;
import unis.project.delta.domain.accountbook.entity.MonthlyFinance;
import unis.project.delta.domain.accountbook.repository.ExpenseBudgetRepository;
import unis.project.delta.domain.accountbook.repository.ExpenseRecordRepository;
import unis.project.delta.domain.accountbook.repository.MonthlyFinanceRepository;
import unis.project.delta.domain.report.dto.response.AnnualReportResponse;
import unis.project.delta.domain.report.dto.response.AnnualReportResponse.*;
import unis.project.delta.domain.report.dto.response.MonthlyReportResponse;
import unis.project.delta.domain.report.dto.response.MonthlyReportResponse.*;
import unis.project.delta.domain.report.dto.response.WeeklyReportResponse;
import unis.project.delta.domain.report.dto.response.WeeklyReportResponse.*;
import unis.project.delta.domain.user.entity.User;
import unis.project.delta.domain.user.repository.UserRepository;
import unis.project.delta.global.exception.CustomException;
import unis.project.delta.global.exception.ErrorCode;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ExpenseRecordRepository expenseRecordRepository;
    private final MonthlyFinanceRepository monthlyFinanceRepository;
    private final ExpenseBudgetRepository expenseBudgetRepository;
    private final UserRepository userRepository;

    // ════════════════════════════════════════
    //  주간 리포트
    // ════════════════════════════════════════

    @Transactional(readOnly = true)
    public WeeklyReportResponse getWeeklyReport(Long userId, LocalDate date) {
        User user = findByUserId(userId);

        // 1. 주 경계 계산 (월~일)
        LocalDate monday = date.with(DayOfWeek.MONDAY);
        LocalDate sunday = date.with(DayOfWeek.SUNDAY);
        LocalDateTime weekStart = monday.atStartOfDay();
        LocalDateTime weekEnd = sunday.plusDays(1).atStartOfDay();

        // 2. 요일별 지출
        List<DailyExpenseDto> dailyExpenses = new ArrayList<>();
        long weeklyTotal = 0;
        String maxDay = "MON";
        long maxAmount = 0;

        for (int i = 0; i < 7; i++) {
            LocalDate d = monday.plusDays(i);
            LocalDateTime dayStart = d.atStartOfDay();
            LocalDateTime dayEnd = d.plusDays(1).atStartOfDay();
            Long amount = expenseRecordRepository.sumAmountByUserAndDateRange(user, dayStart, dayEnd);

            String dow = d.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.US).toUpperCase();
            dailyExpenses.add(new DailyExpenseDto(dow, d.toString(), amount));
            weeklyTotal += amount;

            if (amount > maxAmount) {
                maxAmount = amount;
                maxDay = dow;
            }
        }

        // 3. 지난주 비교
        LocalDateTime lastWeekStart = monday.minusWeeks(1).atStartOfDay();
        LocalDateTime lastWeekEnd = weekStart;
        Long lastWeekTotal = expenseRecordRepository.sumAmountByUserAndDateRange(
                user, lastWeekStart, lastWeekEnd);
        long changeAmount = weeklyTotal - lastWeekTotal;
        double changeRate = lastWeekTotal == 0
                ? 0.0
                : Math.round((double) changeAmount / lastWeekTotal * 1000.0) / 10.0;
        LastWeekComparisonDto comparison = new LastWeekComparisonDto(
                lastWeekTotal, changeAmount, changeRate);

        // 4. 카테고리별 지출
        List<Object[]> categorySums = expenseRecordRepository
                .findCategoryExpenseSums(user, weekStart, weekEnd);

        final long total = weeklyTotal;
        List<CategoryExpenseDto> categoryExpenses = categorySums.stream()
                .map(row -> {
                    Long catId = (Long) row[0];
                    String catName = (String) row[1];
                    Long catAmount = (Long) row[2];
                    double pct = total == 0 ? 0.0 : Math.round((double) catAmount / total * 1000.0) / 10.0;
                    return new CategoryExpenseDto(catId, catName, catAmount, pct);
                })
                .toList();

        // 5. 지출 1위 카테고리
        TopCategoryDto topCategory = categoryExpenses.isEmpty()
                ? new TopCategoryDto(null, null, 0L)
                : new TopCategoryDto(
                categoryExpenses.get(0).categoryId(),
                categoryExpenses.get(0).categoryName(),
                categoryExpenses.get(0).amount());

        // 6. 또래 대비 순위
        PeerRankingDto peerRanking = calculatePeerRanking(weekStart, weekEnd, weeklyTotal);

        return WeeklyReportResponse.of(
                monday.toString(), sunday.toString(), dailyExpenses,
                weeklyTotal, maxDay, comparison, topCategory, peerRanking, categoryExpenses);
    }

    // ════════════════════════════════════════
    //  월간 리포트
    // ════════════════════════════════════════

    @Transactional(readOnly = true)
    public MonthlyReportResponse getMonthlyReport(Long userId, String month) {
        User user = findByUserId(userId);
        YearMonth ym = YearMonth.parse(month);
        LocalDateTime monthStart = ym.atDay(1).atStartOfDay();
        LocalDateTime monthEnd = ym.plusMonths(1).atDay(1).atStartOfDay();

        // 예산 정보
        MonthlyFinance mf = monthlyFinanceRepository.findByUserAndTargetMonth(user, month)
                .orElseThrow(() -> new CustomException(ErrorCode.MONTHLY_FINANCE_NOT_FOUND));

        Long totalSpent = expenseRecordRepository.sumAmountByUserAndDateRange(
                user, monthStart, monthEnd);

        // 카테고리별 지출 Top 3
        List<Object[]> categorySums = expenseRecordRepository
                .findCategoryExpenseSums(user, monthStart, monthEnd);

        int rank = 0;
        List<RankedCategoryDto> topCategories = new ArrayList<>();
        for (Object[] row : categorySums) {
            if (++rank > 3) break;
            Long catAmount = (Long) row[2];
            double pct = totalSpent == 0 ? 0.0
                    : Math.round((double) catAmount / totalSpent * 1000.0) / 10.0;
            topCategories.add(new RankedCategoryDto(
                    rank, (Long) row[0], (String) row[1], catAmount, pct));
        }

        // 가장 큰 지출 Top 3
        List<ExpenseRecord> topRecords = expenseRecordRepository
                .findTopExpenses(user, monthStart, monthEnd);
        List<TopExpenseDto> topExpenses = topRecords.stream()
                .limit(3)
                .map(r -> new TopExpenseDto(
                        r.getId(), r.getPlaceName(), r.getAmount(),
                        r.getExpenseCategory().getName(), r.getExpenseDate().toString()))
                .toList();

        // 지난달 비교
        String lastMonth = ym.minusMonths(1).toString();
        LocalDateTime lastMonthStart = ym.minusMonths(1).atDay(1).atStartOfDay();
        Long lastMonthSpent = expenseRecordRepository.sumAmountByUserAndDateRange(
                user, lastMonthStart, monthStart);
        LastMonthComparisonDto lastMonthComparison = new LastMonthComparisonDto(
                lastMonthSpent, totalSpent - lastMonthSpent);

        return MonthlyReportResponse.of(
                month, mf.getTotalExpenseBudget(), totalSpent,
                topCategories, topExpenses, lastMonthComparison);
    }

    // ════════════════════════════════════════
    //  연간 리포트
    // ════════════════════════════════════════

    @Transactional(readOnly = true)
    public AnnualReportResponse getAnnualReport(Long userId, Integer year) {
        User user = findByUserId(userId);

        long annualTotalSpent = 0;
        long annualTotalBudget = 0;
        String highestMonth = null;
        long highestAmount = -1;
        String lowestMonth = null;
        long lowestAmount = Long.MAX_VALUE;

        List<MonthlyExpenseDto> monthlyExpenses = new ArrayList<>();

        // 1~12월 순회
        for (int m = 1; m <= 12; m++) {
            YearMonth ym = YearMonth.of(year, m);
            String monthStr = ym.toString();
            LocalDateTime monthStart = ym.atDay(1).atStartOfDay();
            LocalDateTime monthEnd = ym.plusMonths(1).atDay(1).atStartOfDay();

            Long spent = expenseRecordRepository.sumAmountByUserAndDateRange(
                    user, monthStart, monthEnd);
            Long budget = monthlyFinanceRepository.findByUserAndTargetMonth(user, monthStr)
                    .map(MonthlyFinance::getTotalExpenseBudget)
                    .orElse(0L);

            monthlyExpenses.add(new MonthlyExpenseDto(monthStr, spent, budget));
            annualTotalSpent += spent;
            annualTotalBudget += budget;

            if (spent > 0 && spent > highestAmount) {
                highestAmount = spent;
                highestMonth = monthStr;
            }
            if (spent > 0 && spent < lowestAmount) {
                lowestAmount = spent;
                lowestMonth = monthStr;
            }
        }

        long totalSaved = annualTotalBudget - annualTotalSpent;
        AnnualSummaryDto summary = new AnnualSummaryDto(
                annualTotalSpent, annualTotalBudget, totalSaved, highestMonth, lowestMonth);

        // 카테고리별 절약 금액 = 연간 카테고리 예산 합 - 연간 카테고리 실제 지출 합
        List<CategorySavingsDto> categorySavings = calculateCategorySavings(user, year);

        return AnnualReportResponse.of(year, monthlyExpenses, summary, categorySavings);
    }

    // ── private helpers ──

    private User findByUserId(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    private PeerRankingDto calculatePeerRanking(LocalDateTime start, LocalDateTime end,
                                                long userTotal) {
        Long totalUsers = expenseRecordRepository.countDistinctUsersByDateRange(start, end);
        if (totalUsers == null || totalUsers <= 1) {
            return new PeerRankingDto(1);
        }
        Long usersAbove = expenseRecordRepository.countUsersWithMoreSpending(
                start, end, userTotal);
        // 나보다 많이 쓴 사람 수 + 1 = 내 순위 → 백분율
        int percentile = (int) Math.round(((double) (usersAbove + 1) / totalUsers) * 100);
        return new PeerRankingDto(Math.min(percentile, 100));
    }

    private List<CategorySavingsDto> calculateCategorySavings(User user, int year) {
        LocalDateTime yearStart = YearMonth.of(year, 1).atDay(1).atStartOfDay();
        LocalDateTime yearEnd = YearMonth.of(year + 1, 1).atDay(1).atStartOfDay();

        // 연간 카테고리별 실제 지출
        Map<Long, Long> actualByCategory = expenseRecordRepository
                .findCategoryExpenseSums(user, yearStart, yearEnd)
                .stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> (Long) row[2]
                ));

        // 연간 카테고리별 예산 합산
        Map<Long, String> categoryNames = new HashMap<>();
        Map<Long, Long> budgetByCategory = new HashMap<>();

        for (int m = 1; m <= 12; m++) {
            String monthStr = YearMonth.of(year, m).toString();
            monthlyFinanceRepository.findByUserAndTargetMonth(user, monthStr)
                    .ifPresent(mf -> {
                        List<ExpenseBudget> budgets = expenseBudgetRepository.findByMonthlyFinance(mf);
                        for (ExpenseBudget eb : budgets) {
                            Long catId = eb.getExpenseCategory().getId();
                            categoryNames.putIfAbsent(catId, eb.getExpenseCategory().getName());
                            budgetByCategory.merge(catId, eb.getAmount(), Long::sum);
                        }
                    });
        }

        return budgetByCategory.entrySet().stream()
                .map(entry -> {
                    Long catId = entry.getKey();
                    Long budget = entry.getValue();
                    Long actual = actualByCategory.getOrDefault(catId, 0L);
                    return new CategorySavingsDto(catId, categoryNames.get(catId), budget - actual);
                })
                .toList();
    }
}
