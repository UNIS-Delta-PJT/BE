package unis.project.delta.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // ── Auth ──
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다."),
    INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않거나 만료된 Access Token입니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 Refresh Token입니다."),
    EXPIRED_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "Refresh Token이 만료되었습니다. 다시 로그인해 주세요."),
    KAKAO_AUTHENTICATION_FAILED(HttpStatus.UNAUTHORIZED, "카카오 인증에 실패했습니다."),

    // ── User ──
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "잘못된 입력값입니다."),

    // ── Mission ──
    ALREADY_ATTENDED(HttpStatus.CONFLICT, "오늘은 이미 출석체크를 완료했습니다."),
    MISSION_NOT_COMPLETED(HttpStatus.BAD_REQUEST, "미션을 아직 달성하지 않았습니다."),
    ALREADY_REWARDED(HttpStatus.CONFLICT, "이미 리워드를 수령했습니다."),

    // ── Finance ──
    MONTHLY_FINANCE_NOT_FOUND(HttpStatus.NOT_FOUND, "예산 설정 정보가 존재하지 않습니다."),
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 지출 카테고리입니다."),
    DUPLICATE_CATEGORY_NAME(HttpStatus.CONFLICT, "이미 존재하는 카테고리명입니다."),
    DEFAULT_CATEGORY_NOT_MODIFIABLE(HttpStatus.BAD_REQUEST, "기본 제공 카테고리는 수정/삭제할 수 없습니다."),
    BUDGET_SUM_MISMATCH(HttpStatus.BAD_REQUEST, "카테고리별 지출 예산 합계가 총 지출 예산과 일치하지 않습니다."),
    INVALID_AMOUNT(HttpStatus.BAD_REQUEST, "금액은 0 이상이어야 합니다."),

    // ── Quiz ──
    QUIZ_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 퀴즈입니다."),
    ALREADY_SUBMITTED(HttpStatus.CONFLICT, "이미 제출했습니다."),

    // ── Map ──
    DICE_NOT_ENABLED(HttpStatus.FORBIDDEN, "주사위를 굴릴 수 없습니다. 금융 퀴즈를 먼저 풀어주세요."),
    INVALID_MAP_POSITION(HttpStatus.BAD_REQUEST, "맵 위치가 유효하지 않습니다."),

    // ── Group ──
    GROUP_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 그룹입니다."),
    GROUP_MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 그룹에 가입되어 있지 않습니다."),
    GROUP_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "그룹은 최대 4개까지 가입할 수 있습니다."),
    ALREADY_JOINED(HttpStatus.CONFLICT, "이미 가입된 그룹입니다."),

    // ── Item ──
    ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 아이템입니다."),
    USER_ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "보유하지 않은 아이템입니다."),
    INSUFFICIENT_COIN(HttpStatus.BAD_REQUEST, "코인이 부족합니다."),
    ALREADY_OWNED(HttpStatus.CONFLICT, "이미 보유 중인 아이템입니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
