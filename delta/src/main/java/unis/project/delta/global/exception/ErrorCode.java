package unis.project.delta.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    // Default
    INTERNAL_SERVER_ERROR(500, "서버 내부에서 알 수 없는 오류가 발생했습니다. 잠시 후 다시 시도해주세요."),
    BAD_REQUEST(400, "요청 형식이 올바르지 않습니다."),
    ERROR(400, "요청 처리에 실패했습니다."),

    // Auth / User
    MISSING_AUTHORIZATION_HEADER(401, "인증 헤더(UUID)가 누락되었습니다."),
    USER_NOT_FOUND(404, "존재하지 않는 사용자입니다."),
    USER_MISMATCH(403, "해당 권한이 없습니다."),

    // Category
    CATEGORY_NOT_FOUND(404, "존재하지 않는 카테고리입니다."),
    DUPLICATE_CATEGORY(409, "이미 존재하는 카테고리입니다."),
    DEFAULT_CATEGORY_NOT_MODIFIABLE(403, "기본 제공 카테고리는 수정할 수 없습니다."),

    // Budget
    MONTHLY_BUDGET_NOT_FOUND(404, "해당 월의 예산이 존재하지 않습니다."),
    CATEGORY_BUDGET_MISMATCH(400, "카테고리별 예산 총합이 등록 예산과 같지 않습니다."),
    DUPLICATE_YEAR_MONTH(409, "이미 등록된 월 예산이 있습니다."),

    // Expense
    INVALID_AMOUNT(400, "소비 금액은 0원보다 커야 합니다.");


    private final int status;
    private final String message;
}
