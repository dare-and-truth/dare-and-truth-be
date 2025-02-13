package PNV.DareAndTruth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import PNV.DareAndTruth.dto.response.ApiStatus;
import PNV.DareAndTruth.dto.response.AppApiResponse;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity<AppApiResponse<Void>> handleRuntimeException(RuntimeException exception) {
        AppApiResponse<Void> appApiResponse = AppApiResponse.<Void>builder()
                .code(500)
                .status(ApiStatus.FAIL)
                .message(exception.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(appApiResponse);
    }

    @ExceptionHandler(value = AppException.class)
    public ResponseEntity<AppApiResponse<Void>> handleAppException(AppException exception) {
        ErrorCode errorCode = exception.getErrorCode();
        AppApiResponse<Void> appApiResponse = AppApiResponse.<Void>builder()
                .code(errorCode.getCode())
                .status(ApiStatus.FAIL)
                .message(errorCode.getMessage())
                .build();
        return ResponseEntity.status(exception.getHttpStatus()).body(appApiResponse);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<AppApiResponse<Void>> handleValidationException(MethodArgumentNotValidException exception) {
        String enumService =
                exception.getFieldError() != null ? exception.getFieldError().getDefaultMessage() : "";
        ErrorCode errorCode;
        try {
            errorCode = ErrorCode.valueOf(enumService);
        } catch (IllegalArgumentException e) {
            AppApiResponse<Void> appApiResponse = AppApiResponse.<Void>builder()
                    .code(500)
                    .status(ApiStatus.FAIL)
                    .message(exception.getMessage())
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(appApiResponse);
        }
        AppApiResponse<Void> appApiResponse = AppApiResponse.<Void>builder()
                .code(errorCode.getCode())
                .status(ApiStatus.FAIL)
                .message(errorCode.getMessage())
                .build();
        return ResponseEntity.badRequest().body(appApiResponse);
    }    
}
