package unis.project.delta.global.exception.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

@Getter
// data가 null일 때는 JSON 결과창에 "data": null 로 안 뜨고 필드 자체가 안 보이게 가려줌
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private final boolean success;
    private final T data;
    private final String errorCode;
    private final String message;

    // 성공 응답용 생성자
    private ApiResponse(T data, String message) {
        this.success = true;
        this.data = data;
        this.errorCode = null;
        this.message = message;
    }

    // 실패 응답용 생성자
    private ApiResponse(String errorCode, String message) {
        this.success = false;
        this.data = null;
        this.errorCode = errorCode;
        this.message = message;
    }

    // 컨트롤러에서 성공했을 때 사용
    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(data, message);
    }

    // 예외 핸들러에서 실패했을 때 사용
    public static <T> ApiResponse<T> fail(String errorCode, String message) {
        return new ApiResponse<>(errorCode, message);
    }
}