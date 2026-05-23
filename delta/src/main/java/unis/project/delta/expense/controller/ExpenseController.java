package unis.project.delta.expense.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import unis.project.delta.expense.dto.request.CreateExpenseRequest;
import unis.project.delta.expense.dto.response.CreateExpenseResponse;
import unis.project.delta.expense.dto.response.GetExpenseListResponse;
import unis.project.delta.expense.service.ExpenseService;
import unis.project.delta.global.exception.dto.ApiResponse; // 팀 공통 포맷 바인딩 가정
import unis.project.delta.global.exception.CustomException;
import unis.project.delta.global.exception.ErrorCode;

@RestController
@RequestMapping("/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;

    @PostMapping
    public ResponseEntity<ApiResponse<CreateExpenseResponse>> createExpense(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @Valid @RequestBody CreateExpenseRequest request) {

        String uuid = extractUuid(authorizationHeader);

        CreateExpenseResponse response = expenseService.createExpense(uuid, request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "소비 기록 등록 성공"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<GetExpenseListResponse>> getExpenseList(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @RequestParam(value = "yearMonth") String yearMonth) {

        String uuid = extractUuid(authorizationHeader);

        GetExpenseListResponse response = expenseService.getExpenseList(uuid, yearMonth);

        return ResponseEntity.ok(ApiResponse.success(response, "소비 기록 목록 조회 성공"));
    }

    private String extractUuid(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new CustomException(ErrorCode.MISSING_AUTHORIZATION_HEADER);
        }
        return authorizationHeader.substring(7);
    }
}