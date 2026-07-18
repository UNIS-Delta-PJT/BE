package unis.project.delta.global.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import unis.project.delta.global.response.ErrorDto;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorDto> handleCustomException(CustomException e, HttpServletRequest request) {
        ErrorCode errorCode = e.getErrorCode();
        ErrorDto errorDto = ErrorDto.builder()
                .timestamp(LocalDateTime.now())
                .status(errorCode.getHttpStatus().value())
                .error(errorCode.getHttpStatus().getReasonPhrase())
                .code(errorCode.name())
                .message(errorCode.getMessage())
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(errorCode.getHttpStatus()).body(errorDto);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDto> handleValidationException(MethodArgumentNotValidException e,
                                                              HttpServletRequest request) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getDefaultMessage())
                .orElse("잘못된 입력값입니다.");

        ErrorDto errorDto = ErrorDto.builder()
                .timestamp(LocalDateTime.now())
                .status(400)
                .error("Bad Request")
                .code(ErrorCode.INVALID_INPUT_VALUE.name())
                .message(message)
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.badRequest().body(errorDto);
    }
}
