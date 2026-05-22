package unis.project.delta.budget.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import unis.project.delta.budget.dto.request.CreateBudgetRequest;
import unis.project.delta.budget.dto.response.MonthlyBudgetResponse;
import unis.project.delta.budget.service.MonthlyBudgetService;
import unis.project.delta.global.exception.CustomException;
import unis.project.delta.global.exception.ErrorCode;
import unis.project.delta.global.exception.dto.ApiResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/budgets/monthly")
public class MonthlyBudgetController {
    private final MonthlyBudgetService monthlyBudgetService;

    // 월예산 생성
    @PostMapping
    public ResponseEntity<ApiResponse<MonthlyBudgetResponse>> createMonthlyBudget (
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @Valid @RequestBody CreateBudgetRequest request) {
        String uuid = extractUuid(authorizationHeader);
        MonthlyBudgetResponse response = monthlyBudgetService.createMonthlyBudget(uuid, request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "월 예산 등록 성공"));
    }

    // TODO: 월예산 조회
    @GetMapping
    public ResponseEntity<ApiResponse<MonthlyBudgetResponse>> getMonthlyBudget(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @RequestParam(value = "yearMonth") String yearMonth) {
        String uuid = extractUuid(authorizationHeader);
        MonthlyBudgetResponse response = monthlyBudgetService.getMonthlyBudget(uuid, yearMonth);

        return ResponseEntity.ok(ApiResponse.success(response, "월 예산 조회 성공"));
    }



    // TODO: 월예산 수정


    private String extractUuid(String header) {
        if(header == null || !header.startsWith("Bearer ")) {
            throw new CustomException(ErrorCode.MISSING_AUTHORIZATION_HEADER);
        }
        return header.substring(7);
    }
}
