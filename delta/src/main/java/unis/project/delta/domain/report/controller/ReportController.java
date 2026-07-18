package unis.project.delta.domain.report.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import unis.project.delta.domain.report.dto.response.AnnualReportResponse;
import unis.project.delta.domain.report.dto.response.MonthlyReportResponse;
import unis.project.delta.domain.report.dto.response.WeeklyReportResponse;
import unis.project.delta.domain.report.service.ReportService;
import unis.project.delta.global.response.ApiResponse;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reports")
public class ReportController {

    private final ReportService reportService;

    /**
     * 주간 리포트 조회.
     * 지정 날짜가 속한 주(월~일)의 소비 리포트를 반환한다.
     */
    @GetMapping("/weekly")
    public ResponseEntity<ApiResponse<WeeklyReportResponse>> getWeeklyReport(
            @AuthenticationPrincipal Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        WeeklyReportResponse response = reportService.getWeeklyReport(userId, date);
        return ResponseEntity.ok(ApiResponse.success(response, "주간 리포트 조회 성공"));
    }

    /**
     * 월간 리포트 조회.
     */
    @GetMapping("/monthly")
    public ResponseEntity<ApiResponse<MonthlyReportResponse>> getMonthlyReport(
            @AuthenticationPrincipal Long userId,
            @RequestParam String month) {

        MonthlyReportResponse response = reportService.getMonthlyReport(userId, month);
        return ResponseEntity.ok(ApiResponse.success(response, "월간 리포트 조회 성공"));
    }

    /**
     * 연간 리포트 조회.
     */
    @GetMapping("/annual")
    public ResponseEntity<ApiResponse<AnnualReportResponse>> getAnnualReport(
            @AuthenticationPrincipal Long userId,
            @RequestParam Integer year) {

        AnnualReportResponse response = reportService.getAnnualReport(userId, year);
        return ResponseEntity.ok(ApiResponse.success(response, "연간 리포트 조회 성공"));
    }
}
