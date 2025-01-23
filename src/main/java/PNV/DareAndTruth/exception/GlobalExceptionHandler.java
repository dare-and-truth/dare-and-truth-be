package PNV.DareAndTruth.exception;

import PNV.DareAndTruth.dto.response.ApiResponse;
import PNV.DareAndTruth.dto.response.ApiStatus;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity<ApiResponse<Void>> handleRuntimeException(RuntimeException exception) {
        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .code(500)
                .status(ApiStatus.FAIL)
                .message(exception.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
    }

    @ExceptionHandler(value = AppException.class)
    public ResponseEntity<ApiResponse<Void>> handleAppException(AppException exception) {
        ErrorCode errorCode = exception.getErrorCode();
        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .code(errorCode.getErrorCode())
                .status(ApiStatus.FAIL)
                .message(errorCode.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(apiResponse);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(MethodArgumentNotValidException exception) {
        String enumService = exception.getFieldError() != null ? exception.getFieldError().getDefaultMessage() : "";
        ErrorCode errorCode;
        try {
            errorCode = ErrorCode.valueOf(enumService);
        } catch (IllegalArgumentException e) {
            ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                    .code(500)
                    .status(ApiStatus.FAIL)
                    .message(exception.getMessage())
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
        }
        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .code(errorCode.getErrorCode())
                .status(ApiStatus.FAIL)
                .message(errorCode.getMessage())
                .build();
        return ResponseEntity.badRequest().body(apiResponse);
    }
}